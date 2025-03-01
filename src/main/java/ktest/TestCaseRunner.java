package ktest;

import com.google.common.base.Strings;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import ktest.core.LogTab;
import ktest.domain.Action;
import ktest.domain.TestCase;
import ktest.domain.TestRecord;
import ktest.domain.xlsx.Matrix;
import ktest.domain.xunit.XUnitReport;
import ktest.domain.xunit.XUnitSuite;
import ktest.kafka.ClusterClient;
import ktest.kafka.TopicRef;
import ktest.script.Engine;
import ktest.script.ScriptException;
import ktest.script.func.GotoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ktest.core.AnsiColor.*;

@Dependent
class TestCaseRunner implements Callable<XUnitReport> {
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseRunner.class);

    private final Engine engine;
    private final ClusterClient kafkaClient;

    private TestCase testCase;
    private int autoPause;
    private int backOffset;
    private LogTab logTab = new LogTab(true);

    private String currentVariables = "";

    private ParallelState parallelState;

    @Inject
    TestCaseRunner(final Engine pEngine, final ClusterClient pKafkaClient) {
        engine = pEngine;
        kafkaClient = pKafkaClient;
    }

    TestCaseRunner testCase(final TestCase pTestCase) {
        testCase = pTestCase;
        return this;
    }

    TestCaseRunner autoPause(final int pAutoPause) {
        autoPause = pAutoPause;
        return this;
    }

    TestCaseRunner backOffset(final int pBackOffset) {
        backOffset = pBackOffset;
        return this;
    }

    TestCaseRunner logTab(final LogTab pLogTab) {
        logTab = pLogTab;
        return this;
    }

    TestCaseRunner parallelState(final ParallelState pState) {
        parallelState = pState;
        return this;
    }

    Engine engine() {
        return engine;
    }

    @Override
    public XUnitReport call() {
        final var tags = testCase.tags();
        LOG.info("{}Start Test Case: {}{}", logTab.tab(WHITE), testCase.name(), tags == null || tags.isEmpty() ? "" : " " + tags);
        final var xUnitReport = new XUnitReport();
        final var xUnitSuite = xUnitReport.startNewSuite(testCase.name());
        engine.eval(testCase.beforeAllScript());
        evalScript(engine, "beforeAll", testCase.beforeAllScript());
        logTab.inc();
        executeTestCase(engine, testCase, xUnitSuite);
        logTab.dec();
        evalScript(engine, "afterAll", testCase.afterAllScript());
        xUnitSuite.end();
        xUnitReport.end();

        if (LOG.isInfoEnabled()) {
            LOG.info("{}End Test Case: {} ({}/{})", logTab.tab(WHITE), testCase.name(),
                    parallelState != null ? parallelState.endedCount.incrementAndGet() : "?", parallelState != null ? parallelState.subTasks.size() : "?");
        }
        return xUnitReport;
    }

    public static void logOptions(final List<TestCase> pTestCases, final String pEnv, final String pTagsOption) {
        LOG.info("");
        LOG.info("Run options: -e {}{}", pEnv, pTagsOption == null || pTagsOption.isEmpty() ? "" : " -t " + pTagsOption);
        final var availableTags = pTestCases.stream()
                .filter(t -> t.tags() != null)
                .flatMap(t -> t.tags().stream())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().toList();
        LOG.info("Known tags: {}", String.join(",", availableTags));
    }

    public static void logSynthesis(final XUnitReport pReport) {
        LOG.info("");
        LOG.info("{}SUMMARY:", WHITE);
        final var successes = new TreeSet<>();
        final var failures = new TreeSet<>();
        pReport.testsuite.forEach(ts -> {
            if (ts.failures() == 0 && ts.errors() == 0) {
                successes.add(ts.name);
            } else {
                final var failedStep = ts.testcase.stream()
                        .filter(s -> s.failure != null || s.error != null)
                        .findFirst().map(s -> s.name).orElse("");
                failures.add(ts.name + " @ " + failedStep);
            }
        });
        final var successCount = successes.size();
        if (successCount > 0) {
            LOG.info("{} - Success: {}", GREEN, successCount);
            successes.forEach(n -> LOG.info("{}    - {}", GREEN, n));
        }
        final var failureCount = failures.size();
        if (failureCount > 0) {
            LOG.warn("{} - Failure: {}", RED, failureCount);
            failures.forEach(n -> LOG.warn("{}    - {}", RED, n));
        }
    }

    void evalScript(final Engine pEngine, final String pName, final List<String> pLines) {
        if (pLines.isEmpty()) {
            return;
        }
        LOG.debug("{}Executing {} script...", logTab.tab(LIGHTGRAY), pName);
        pEngine.eval(pLines);
        final var variables = pEngine.context().variables();
        final var newVariables = String.join("\n", variables.stream().map(e -> e.getKey() + ':' + e.getValue()).toList());
        if (!newVariables.equals(currentVariables)) {
            currentVariables = newVariables;
            LOG.debug("{}Variables:", logTab.tab(LIGHTGRAY));
            logTab.inc();
            variables.forEach(e -> LOG.debug("{}{} = {}", logTab.tab(LIGHTGRAY), e.getKey(), e.getValue()));
            logTab.dec();
        }
    }

    boolean executeTestCase(final Engine pEngine, final TestCase pTestCase, final XUnitSuite pTestSuite) {
        Action lastAction = null;
        var skipAfterFailureOrError = false;
        final var steps = pTestCase.steps();
        for (int i = 0; i < steps.size(); i++) {
            var step = steps.get(i);
            final var action = step.action();
            final var xUnitCase = pTestSuite.startNewCase(step.name() + " (" + action + ")", pTestCase.name(), action == Action.PRESENT || action == Action.ABSENT);
            try {
                LOG.info("{}- Step : {} ({})", logTab.tab(action == Action.TODO || skipAfterFailureOrError ? BRIGHTYELLOW : WHITE), step.name(), action);
                pEngine.context().disablePause(skipAfterFailureOrError).lastRecord(null);
                evalScript(pEngine, "before", step.beforeScript());
                pEngine.context().variables().forEach(e -> xUnitCase.addProperty(e.getKey(), e.getValue().value().toString()));
                final var topicRef = new TopicRef(pEngine.evalInLine(step.broker()), pEngine.evalInLine(step.topic()), step.keySerde(), step.valueSerde());
                final var stepState = Matrix.add(pTestCase.name(), topicRef, action, pTestCase.tags(), Thread.currentThread().threadId());
                if (skipAfterFailureOrError) {
                    xUnitCase.skip("Skipped because of previous failure or error.");
                    continue;
                }
                if (action == Action.TODO) {
                    xUnitCase.skip("Marked as TODO");
                    stepState.success();
                } else {
                    logTab.inc();
                    LOG.debug("{}Target: {}", logTab.tab(LIGHTGRAY), topicRef.id());
                    final var parsedRecord = evalInLine(pEngine, step.record());
                    LOG.debug("{}Record: {}", logTab.tab(LIGHTGRAY), parsedRecord);
                    try {
                        if (action == Action.SEND) {
                            kafkaClient.send(topicRef, parsedRecord);
                            stepState.success();
                        } else {
                            if (lastAction == Action.SEND && autoPause > 0) {
                                LOG.debug("{}Auto pause {}ms before assert...", logTab.tab(LIGHTGRAY), autoPause);
                                Thread.sleep(autoPause);
                            }
                            var found = kafkaClient.find(topicRef, parsedRecord, backOffset);
                            pEngine.context().lastRecord(found);
                            if (found == null && action == Action.PRESENT) {
                                LOG.trace("{}Retrying find with larger range...", logTab.tab(LIGHTGRAY));
                                Thread.sleep(250);
                                found = kafkaClient.find(topicRef, parsedRecord, 2 * backOffset);
                            }
                            if ((found != null && action == Action.ABSENT) || (found == null && action == Action.PRESENT)) {
                                LOG.warn("{}{} assertion failed for record {}.", logTab.tab(RED), action, parsedRecord);
                                xUnitCase.fail(action + " assertion failed.", parsedRecord.toString());
                                skipAfterFailureOrError = true;
                            } else {
                                stepState.success();
                            }
                        }
                    } catch (final RuntimeException e) {
                        LOG.error("{}{} error.", logTab.tab(RED), action, e);
                        xUnitCase.error("Action error: " + e.getMessage(), e);
                        skipAfterFailureOrError = true;
                    } catch (final InterruptedException e) {
                        LOG.error("Interrupted!", e);
                        Thread.currentThread().interrupt();
                    }
                    evalScript(pEngine, "after", step.afterScript());
                    logTab.dec();
                }
            } catch (final GotoException e) {
                logTab.dec();
                final var target = IntStream.range(0, steps.size())
                        .filter(idx -> e.stepName().equalsIgnoreCase(steps.get(idx).name()))
                        .findFirst().orElse(-1);
                if (target < 0) {
                    throw new ScriptException("Step '" + e.stepName() + "' not found");
                }
                i = target - 1;
            }
            xUnitCase.end();
            lastAction = action;
        }
        return skipAfterFailureOrError;
    }

    static List<TestCase> filteredByTags(final List<TestCase> pTestCases, final String pTagsOption) {
        if (Strings.isNullOrEmpty(pTagsOption)) {
            return pTestCases;
        }

        final var inclTags = new ArrayList<List<String>>();
        final var exclTags = new ArrayList<String>();
        Arrays.stream(pTagsOption.split(",")).forEach(f -> {
            final var split = f.trim().split("\\+");
            if (split.length > 0) {
                if (split[0].startsWith("!")) {
                    exclTags.add(split[0].substring(1));
                } else {
                    inclTags.add(Arrays.stream(f.trim().split("\\+")).map(String::trim).toList());
                }
            }
        });
        final var res = new ArrayList<TestCase>();
        pTestCases.forEach(pTestCase -> {
            final var testCaseTags = pTestCase.tags();
            final var matchIncluded = inclTags.isEmpty() || (testCaseTags != null && inclTags.stream().anyMatch(testCaseTags::containsAll));
            final var matchExcluded = testCaseTags != null && exclTags.stream().anyMatch(testCaseTags::contains);
            if (matchIncluded && !matchExcluded) {
                res.add(pTestCase);
            }
        });
        return res;
    }

    private TestRecord evalInLine(final Engine pEngine, final TestRecord pRecord) {
        final var headers = new TreeMap<String, String>();
        for (final var e : pRecord.headers().entrySet()) {
            headers.put(e.getKey(), pEngine.evalInLine(e.getValue()));
        }
        return new TestRecord(pEngine.evalInLine(pRecord.timestamp()), headers, pEngine.evalInLine(pRecord.key()), pEngine.evalInLine(pRecord.value()));
    }
}
