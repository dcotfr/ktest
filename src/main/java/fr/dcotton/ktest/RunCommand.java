package fr.dcotton.ktest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.dcotton.ktest.domain.Action;
import fr.dcotton.ktest.domain.TestCase;
import fr.dcotton.ktest.domain.xunit.XUnitReport;
import fr.dcotton.ktest.script.Engine;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.List;

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
        tab++;
        for (final var step : testCase.steps()) {
            final var xUnitCase = xUnitSuite.startNewCase(step.name());
            final var action = step.action();
            LOG.info("{}- Step : {} ({})", tab(action == Action.TODO ? YELLOW : WHITE), step.name(), action);
            if (action == Action.TODO) {
                xUnitCase.skip("Marked as TODO");
            } else {
                tab++;
                evalScript("before", step.beforeScript());
                engine.context().variables().forEach(e -> xUnitCase.addProperty(e.getKey(), e.getValue().value().toString()));
                LOG.debug("{}Broker: {}", tab(GRAY), step.broker());
                LOG.debug("{}Topic : {}", tab(GRAY), step.topic());
                LOG.debug("{}Record: {}", tab(GRAY), step.record());
                evalScript("after", step.afterScript());
                tab--;
            }
            xUnitCase.end();
        }
        tab--;
        evalScript("afterAll", testCase.afterAllScript());
        xUnitSuite.end();
        xUnitReport.end();
        final var xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        try {
            System.out.println(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(xUnitReport));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

    private String tab(final String pColor) {
        return " ".repeat(tab * 2) + pColor;
    }
}
