package ktest;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import ktest.core.LogTab;
import ktest.domain.Action;
import ktest.domain.TestCase;
import ktest.domain.TestRecord;
import ktest.domain.xunit.XUnitReport;
import ktest.domain.xunit.XUnitSuite;
import ktest.kafka.ClusterClient;
import ktest.kafka.TopicRef;
import ktest.script.Engine;
import ktest.script.ScriptException;
import ktest.script.func.GotoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static ktest.core.AnsiColor.*;

@Dependent
class TestCaseRunner implements Callable<XUnitReport> {
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseRunner.class);

    private final Engine engine;
    private final ClusterClient kafkaClient;

    private TestCase testCase;
    private Integer backOffset;
    private LogTab logTab = new LogTab(true);

    private String currentVariables = "";

    @Inject
    TestCaseRunner(final Engine pEngine, final ClusterClient pKafkaClient) {
        engine = pEngine;
        kafkaClient = pKafkaClient;
    }

    TestCaseRunner testCase(final TestCase pTestCase) {
        testCase = pTestCase;
        return this;
    }

    TestCaseRunner backOffset(final Integer pBackOffset) {
        backOffset = pBackOffset;
        return this;
    }

    TestCaseRunner logTab(final LogTab pLogTab) {
        logTab = pLogTab;
        return this;
    }

    Engine engine() {
        return engine;
    }

    @Override
    public XUnitReport call() {
        LOG.info("{}Start Test Case: {}", logTab.tab(WHITE), testCase.name());
        final var xUnitReport = new XUnitReport();
        final var xUnitSuite = xUnitReport.startNewSuite(testCase.name());
        engine.eval(testCase.beforeAllScript());
        evalScript(engine, "beforeAll", testCase.beforeAllScript());
        logTab.inc();
        executeTestCase(engine, testCase, xUnitSuite);
        logTab.dec();
        xUnitReport.end();

        LOG.info("{}End Test Case: {}", logTab.tab(WHITE), testCase.name());
        return xUnitReport;
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
                failures.add(ts.name);
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
        var skipAfterFailureOrError = false;
        final var steps = pTestCase.steps();
        for (int i = 0; i < steps.size(); i++) {
            var step = steps.get(i);
            final var action = step.action();
            final var xUnitCase = pTestSuite.startNewCase(step.name() + " (" + action + ")", pTestCase.name(), action == Action.PRESENT || action == Action.ABSENT);
            try {
                LOG.info("{}- Step : {} ({})", logTab.tab(action == Action.TODO || skipAfterFailureOrError ? BRIGHTYELLOW : WHITE), step.name(), action);
                if (action == Action.TODO) {
                    xUnitCase.skip("Marked as TODO");
                } else if (skipAfterFailureOrError) {
                    xUnitCase.skip("Skipped because of previous failure or error.");
                } else {
                    logTab.inc();
                    evalScript(pEngine, "before", step.beforeScript());
                    pEngine.context().variables().forEach(e -> xUnitCase.addProperty(e.getKey(), e.getValue().value().toString()));
                    final var topicRef = new TopicRef(pEngine.evalInLine(step.broker()), pEngine.evalInLine(step.topic()), step.keySerde(), step.valueSerde());
                    LOG.debug("{}Target: {}", logTab.tab(LIGHTGRAY), topicRef.id());
                    final var parsedRecord = evalInLine(pEngine, step.record());
                    LOG.debug("{}Record: {}", logTab.tab(LIGHTGRAY), parsedRecord);
                    try {
                        if (action == Action.SEND) {
                            kafkaClient.send(topicRef, parsedRecord);
                        } else {
                            var found = kafkaClient.find(topicRef, parsedRecord, backOffset);
                            if (!found && action == Action.PRESENT) {
                                LOG.trace("{}Retrying find with larger range...", logTab.tab(LIGHTGRAY));
                                Thread.sleep(250);
                                found = kafkaClient.find(topicRef, parsedRecord, 2 * backOffset);
                            }
                            if ((found && action == Action.ABSENT) || (!found && action == Action.PRESENT)) {
                                LOG.warn("{}{} assertion failed for record {}.", logTab.tab(RED), action, parsedRecord);
                                xUnitCase.fail(action + " assertion failed.", parsedRecord.toString());
                                skipAfterFailureOrError = true;
                            }
                        }
                    } catch (final RuntimeException | InterruptedException e) {
                        LOG.error("{}{} error.", logTab.tab(RED), action, e);
                        xUnitCase.error("Action error: " + e.getMessage(), e);
                        skipAfterFailureOrError = true;
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
        }
        return skipAfterFailureOrError;
    }

    private TestRecord evalInLine(final Engine pEngine, final TestRecord pRecord) {
        final var headers = new TreeMap<String, String>();
        for (final var e : pRecord.headers().entrySet()) {
            headers.put(e.getKey(), pEngine.evalInLine(e.getValue()));
        }
        return new TestRecord(pEngine.evalInLine(pRecord.timestamp()), headers, pEngine.evalInLine(pRecord.key()), pEngine.evalInLine(pRecord.value()));
    }
}
