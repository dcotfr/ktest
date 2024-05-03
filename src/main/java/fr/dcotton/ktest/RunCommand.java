package fr.dcotton.ktest;

import fr.dcotton.ktest.core.KTestException;
import fr.dcotton.ktest.domain.Action;
import fr.dcotton.ktest.domain.TestCase;
import fr.dcotton.ktest.domain.TestRecord;
import fr.dcotton.ktest.domain.xunit.XUnitReport;
import fr.dcotton.ktest.kafka.ClusterClient;
import fr.dcotton.ktest.kafka.TopicRef;
import fr.dcotton.ktest.script.Engine;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static fr.dcotton.ktest.MainCommand.VERSION;
import static fr.dcotton.ktest.core.AnsiColor.*;

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

    @CommandLine.Option(names = {"-b", "--back"}, description = "Back offset.", defaultValue = "500")
    private Integer backOffset;

    private String currentVariables = "";
    private int tab;

    @Inject
    Engine engine;

    @Inject
    ClusterClient kafkaClient;

    @Override
    public void run() {
        var finalFailureOrError = false;
        final var testCases = TestCase.load(file);
        final var xUnitReport = new XUnitReport();
        for (final var testCase : testCases) {
            final var xUnitSuite = xUnitReport.startNewSuite(testCase.name());
            LOG.info("{}Test Case: {}", tab(WHITE), testCase.name());
            engine.eval(testCase.beforeAllScript());
            evalScript("beforeAll", testCase.beforeAllScript());
            ++tab;
            var localSkipAfterFailureOrError = false;
            for (final var step : testCase.steps()) {
                final var action = step.action();
                final var xUnitCase = xUnitSuite.startNewCase(step.name() + " (" + action + ')', action == Action.PRESENT || action == Action.ABSENT);
                LOG.info("{}- Step : {} ({})", tab(action == Action.TODO ? BRIGHTYELLOW : WHITE), step.name(), action);
                if (action == Action.TODO) {
                    xUnitCase.skip("Marked as TODO");
                } else if (localSkipAfterFailureOrError) {
                    xUnitCase.skip("Skipped because of previous failure or error.");
                } else {
                    ++tab;
                    evalScript("before", step.beforeScript());
                    engine.context().variables().forEach(e -> xUnitCase.addProperty(e.getKey(), e.getValue().value().toString()));
                    final var parsedTopic = evalInLine(step.topic());
                    final var parsedBroker = evalInLine(step.broker());
                    final var topicRef = new TopicRef(parsedBroker, parsedTopic, step.keySerde(), step.valueSerde());
                    LOG.debug("{}Target: {}", tab(LIGHTGRAY), topicRef.id());
                    final var parsedRecord = evalInLine(step.record());
                    LOG.debug("{}Record: {}", tab(LIGHTGRAY), parsedRecord);
                    xUnitCase.className(testCase.name());
                    try {
                        if (action == Action.SEND) {
                            kafkaClient.send(topicRef, parsedRecord);
                        } else {
                            final var found = kafkaClient.find(topicRef, parsedRecord, backOffset);
                            if ((found && action == Action.ABSENT) || (!found && action == Action.PRESENT)) {
                                LOG.warn("{}{} assertion failed.", tab(RED), action);
                                xUnitCase.fail(action + " assertion failed.", parsedRecord.toString());
                                localSkipAfterFailureOrError = true;
                            }
                        }
                    } catch (final RuntimeException e) {
                        LOG.error("{}{} error.", tab(RED), action, e);
                        xUnitCase.error("Action error: " + e.getMessage(), e);
                        localSkipAfterFailureOrError = true;
                    }
                    evalScript("after", step.afterScript());
                    --tab;
                }
                xUnitCase.end();
            }
            --tab;
            evalScript("afterAll", testCase.afterAllScript());
            xUnitSuite.end();
            if (localSkipAfterFailureOrError) {
                finalFailureOrError = true;
            }
        }
        xUnitReport.end();
        try {
            Files.writeString(Path.of(report), xUnitReport.toXml());
        } catch (IOException e) {
            throw new KTestException("Failed to write test report.", e);
        }
        if (finalFailureOrError) {
            throw new KTestException("Ends with " + xUnitReport.failures() + " failures and " + xUnitReport.errors() + " errors.", null);
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
        return new TestRecord(pRecord.timestamp(), headers, evalInLine(pRecord.key()), evalInLine(pRecord.value()));
    }

    private String tab(final String pColor) {
        return " ".repeat(tab * 2) + pColor;
    }
}
