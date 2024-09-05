package ktest;

import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.domain.Action;
import ktest.domain.TestCase;
import ktest.domain.TestRecord;
import ktest.domain.xunit.XUnitReport;
import ktest.domain.xunit.XUnitSuite;
import ktest.kafka.ClusterClient;
import ktest.kafka.TopicRef;
import ktest.script.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static ktest.MainCommand.VERSION;
import static ktest.core.AnsiColor.*;

@CommandLine.Command(name = "run", description = "Run test case.",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class RunCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RunCommand.class);

    @CommandLine.Option(names = {"-e", "--env"}, description = "Name of the environment to use.", required = true)
    private String env;

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path of test case description file to execute.", defaultValue = "ktestcase.yml")
    private String file;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Path of the config file.", defaultValue = "ktconfig.yml")
    private String config;

    @CommandLine.Option(names = {"-r", "--report"}, description = "Path of the test report file.", defaultValue = "ktreport.xml")
    private String report;

    @CommandLine.Option(names = {"-b", "--back"}, description = "Back offset.", defaultValue = "250")
    private Integer backOffset;

    private String currentVariables = "";
    private int tab;

    private final Engine engine;
    private final ClusterClient kafkaClient;

    @Inject
    RunCommand(final Engine pEngine, final ClusterClient pKafkaClient) {
        engine = pEngine;
        kafkaClient = pKafkaClient;
    }

    @Override
    public void run() {
        var finalFailureOrError = false;
        final var testCases = TestCase.load(file);
        final var xUnitReport = new XUnitReport();
        for (final var testCase : testCases) {
            final var xUnitSuite = xUnitReport.startNewSuite(testCase.name());
            LOG.info("{}Test Case: {}", tab(WHITE), testCase.name());
            engine.reset().eval(testCase.beforeAllScript());
            evalScript("beforeAll", testCase.beforeAllScript());
            ++tab;
            if (executeTestCase(testCase, xUnitSuite)) {
                finalFailureOrError = true;
            }
            --tab;
            evalScript("afterAll", testCase.afterAllScript());
            xUnitSuite.end();
        }
        xUnitReport.end();
        logSynthesis(xUnitReport);
        try {
            Files.writeString(Path.of(report), xUnitReport.toXml());
        } catch (final IOException e) {
            throw new KTestException("Failed to write test report.", e);
        }
        if (finalFailureOrError) {
            throw new TestFailureOrError(xUnitReport);
        }
    }

    private boolean executeTestCase(final TestCase pTestCase, final XUnitSuite pTestSuite) {
        var skipAfterFailureOrError = false;
        for (final var step : pTestCase.steps()) {
            final var action = step.action();
            final var xUnitCase = pTestSuite.startNewCase(step.name() + " (" + action + ')', pTestCase.name(), action == Action.PRESENT || action == Action.ABSENT);
            LOG.info("{}- Step : {} ({})", tab(action == Action.TODO || skipAfterFailureOrError ? BRIGHTYELLOW : WHITE), step.name(), action);
            if (action == Action.TODO) {
                xUnitCase.skip("Marked as TODO");
            } else if (skipAfterFailureOrError) {
                xUnitCase.skip("Skipped because of previous failure or error.");
            } else {
                ++tab;
                evalScript("before", step.beforeScript());
                engine.context().variables().forEach(e -> xUnitCase.addProperty(e.getKey(), e.getValue().value().toString()));
                final var topicRef = new TopicRef(evalInLine(step.broker()), evalInLine(step.topic()), step.keySerde(), step.valueSerde());
                LOG.debug("{}Target: {}", tab(LIGHTGRAY), topicRef.id());
                final var parsedRecord = evalInLine(step.record());
                LOG.debug("{}Record: {}", tab(LIGHTGRAY), parsedRecord);
                try {
                    if (action == Action.SEND) {
                        kafkaClient.send(topicRef, parsedRecord);
                    } else {
                        var found = kafkaClient.find(topicRef, parsedRecord, backOffset);
                        if (!found && action == Action.PRESENT) {
                            LOG.trace("{}Retrying find with larger range...", tab(LIGHTGRAY));
                            Thread.sleep(250);
                            found = kafkaClient.find(topicRef, parsedRecord, 2 * backOffset);
                        }
                        if ((found && action == Action.ABSENT) || (!found && action == Action.PRESENT)) {
                            LOG.warn("{}{} assertion failed for record {}.", tab(RED), action, parsedRecord);
                            xUnitCase.fail(action + " assertion failed.", parsedRecord.toString());
                            skipAfterFailureOrError = true;
                        }
                    }
                } catch (final RuntimeException | InterruptedException e) {
                    LOG.error("{}{} error.", tab(RED), action, e);
                    xUnitCase.error("Action error: " + e.getMessage(), e);
                    skipAfterFailureOrError = true;
                }
                evalScript("after", step.afterScript());
                --tab;
            }
            xUnitCase.end();
        }
        return skipAfterFailureOrError;
    }

    private void logSynthesis(final XUnitReport pReport) {
        LOG.info("{}Synthesis:", tab(WHITE));
        final var successes = new ArrayList<>();
        final var failures = new ArrayList<>();
        pReport.testsuite.forEach(ts -> {
            if (ts.failures() == 0 && ts.errors() == 0) {
                successes.add(ts.name);
            } else {
                failures.add(ts.name);
            }
        });
        if (successes.size() > 0) {
            LOG.info("{} - Successful:", tab(GREEN));
            successes.forEach(n -> LOG.info("{}    - {}", tab(GREEN), n));
        }
        if (failures.size() > 0) {
            LOG.warn("{} - Failed:", tab(RED));
            failures.forEach(n -> LOG.warn("{}    - {}", tab(RED), n));
        }
    }

    private void evalScript(final String pName, final List<String> pLines) {
        if (pLines.isEmpty()) {
            return;
        }
        LOG.debug("{}Executing {} script...", tab(LIGHTGRAY), pName);
        engine.eval(pLines);
        final var variables = engine.context().variables();
        final var newVariables = String.join("\n", variables.stream().map(e -> e.getKey() + ':' + e.getValue()).toList());
        if (!newVariables.equals(currentVariables)) {
            currentVariables = newVariables;
            LOG.debug("{}Variables:", tab(LIGHTGRAY));
            tab++;
            variables.forEach(e -> LOG.debug("{}{} = {}", tab(LIGHTGRAY), e.getKey(), e.getValue()));
            tab--;
        }
    }

    private String evalInLine(final String pAttribute) {
        if (pAttribute == null) {
            return null;
        }

        final var pattern = Pattern.compile("(\\$\\{.*?})");
        final var matcher = pattern.matcher(pAttribute);
        var res = pAttribute;
        while (matcher.find()) {
            final var group = matcher.group();
            final var repl = engine.eval(group.substring(2, group.length() - 1)).toString();
            res = res.replace(group, repl);
        }
        return res;
    }

    private TestRecord evalInLine(final TestRecord pRecord) {
        final var headers = new TreeMap<String, String>();
        for (final var e : pRecord.headers().entrySet()) {
            headers.put(e.getKey(), evalInLine(e.getValue()));
        }
        return new TestRecord(evalInLine(pRecord.timestamp()), headers, evalInLine(pRecord.key()), evalInLine(pRecord.value()));
    }

    private String tab(final String pColor) {
        return " ".repeat(tab * 2) + pColor;
    }
}
