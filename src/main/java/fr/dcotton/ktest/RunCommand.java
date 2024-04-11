package fr.dcotton.ktest;

import fr.dcotton.ktest.domain.Action;
import fr.dcotton.ktest.domain.Record;
import fr.dcotton.ktest.domain.TestCase;
import fr.dcotton.ktest.domain.xunit.XUnitReport;
import fr.dcotton.ktest.script.Engine;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

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

    private String currentVariables = "";
    private int tab;

    @Inject
    Engine engine;

    @Override
    public void run() {
        final var testCase = TestCase.load(file);
        final var xUnitReport = new XUnitReport();
        final var xUnitSuite = xUnitReport.startNewSuite(testCase.name());
        LOG.info("{}Test Case: {}", tab(WHITE), testCase.name());
        engine.eval(testCase.beforeAllScript());
        evalScript("beforeAll", testCase.beforeAllScript());
        ++tab;
        for (final var step : testCase.steps()) {
            final var xUnitCase = xUnitSuite.startNewCase(step.name());
            final var action = step.action();
            LOG.info("{}- Step : {} ({})", tab(action == Action.TODO ? YELLOW : WHITE), step.name(), action);
            if (action == Action.TODO) {
                xUnitCase.skip("Marked as TODO");
            } else {
                ++tab;
                evalScript("before", step.beforeScript());
                engine.context().variables().forEach(e -> xUnitCase.addProperty(e.getKey(), e.getValue().value().toString()));
                final var parsedTopic = evalInLine(step.topic());
                final var parsedBroker = evalInLine(step.broker());
                LOG.debug("{}Target: {}", tab(GRAY), parsedTopic + '@' + parsedBroker);
                final var parsedRecord = evalInLine(step.record());
                LOG.debug("{}Record: {}", tab(GRAY), parsedRecord);
                xUnitCase.details(action, parsedBroker, parsedTopic);
                xUnitCase.addProperty("$record", parsedRecord.toString());
                evalScript("after", step.afterScript());
                --tab;
            }
            xUnitCase.end();
        }
        --tab;
        evalScript("afterAll", testCase.afterAllScript());
        xUnitSuite.end();
        xUnitReport.end();
        System.err.println(xUnitReport.toXml());
    }

    private void evalScript(final String pName, final List<String> pLines) {
        if (pLines.isEmpty()) {
            return;
        }
        LOG.debug("{}Executing {} script...", tab(GRAY), pName);
        engine.eval(pLines);
        final var variables = engine.context().variables();
        final var newVariables = String.join("\n", variables.stream().map(e -> e.getKey() + ':' + e.getValue()).toList());
        if (!newVariables.equals(currentVariables)) {
            currentVariables = newVariables;
            LOG.debug("{}Variables:", tab(GRAY));
            tab++;
            variables.forEach(e -> LOG.debug("{}{} = {}", tab(GRAY), e.getKey(), e.getValue()));
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

    private Record evalInLine(final Record pRecord) {
        final var headers = new TreeMap<String, String>();
        for (final var e : pRecord.headers().entrySet()) {
            headers.put(e.getKey(), evalInLine(e.getValue()));
        }
        return new Record(pRecord.timestamp(), headers, evalInLine(pRecord.key()), evalInLine(pRecord.value()));
    }

    private String tab(final String pColor) {
        return " ".repeat(tab * 2) + pColor;
    }
}
