package ktest;

import com.google.common.base.Strings;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.core.LogTab;
import ktest.domain.TestCase;
import ktest.domain.xlsx.Matrix;
import ktest.domain.xunit.XUnitReport;
import ktest.kafka.ClusterClient;
import ktest.script.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static ktest.MainCommand.VERSION;
import static ktest.TestCaseRunner.filteredByTags;
import static ktest.TestCaseRunner.logOptions;
import static ktest.core.AnsiColor.WHITE;

@CommandLine.Command(name = "srun", description = "Sequential run of test case(s).",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class SRunCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SRunCommand.class);

    @CommandLine.Option(names = {"-e", "--env"}, description = "Name of the environment to use.", required = true)
    private String env;

    @CommandLine.Option(names = {"-f", "--file"}, description = "Path of test case description file to execute.", defaultValue = "ktestcase.yml")
    private String file;

    @CommandLine.Option(names = {"-c", "--config"}, description = "Path of the config file.", defaultValue = "ktconfig.yml")
    private String config;

    @CommandLine.Option(names = {"-r", "--report"}, description = "Path of the test report file (JUnit format).", defaultValue = "ktreport.xml")
    private String report;

    @CommandLine.Option(names = {"-b", "--back"}, description = "Back offset.", defaultValue = "250")
    private Integer backOffset;

    @CommandLine.Option(names = {"-t", "--tags"}, description = "Tags to filter test cases to run.")
    private String tags;

    @CommandLine.Option(names = {"-m", "--matrix"}, description = "Path of the matrix summary file (xlsx format).", defaultValue = "ktmatrix.xlsx")
    private String matrix;

    private final Instance<Engine> engineFactory;
    private final Instance<ClusterClient> kafkaClientFactory;
    private final LogTab logTab = new LogTab(false);

    @Inject
    SRunCommand(final Instance<Engine> pEngineFactory, final Instance<ClusterClient> pKafkaClientFactory) {
        engineFactory = pEngineFactory;
        kafkaClientFactory = pKafkaClientFactory;
    }

    @Override
    public void run() {
        final var engine = engineFactory.get();
        final var testCaseRunner = new TestCaseRunner(engine, kafkaClientFactory.get()).backOffset(backOffset).logTab(logTab);
        var finalFailureOrError = false;
        final var testCases = TestCase.load(file);
        if (!Strings.isNullOrEmpty(tags)) {
            LOG.info("{}Filtering Test Cases by: {}", WHITE, tags);
        }
        final var xUnitReport = new XUnitReport();
        final var globalVariables = engine.reset().context().variables();
        for (final var testCase : filteredByTags(testCases, tags)) {
            final var xUnitSuite = xUnitReport.startNewSuite(testCase.name());
            final var stepTags = testCase.tags();
            LOG.info("{}Test Case: {}{}", logTab.tab(WHITE), testCase.name(), stepTags == null || stepTags.isEmpty() ? "" : " " + stepTags);
            engine.init(globalVariables);
            testCaseRunner.evalScript(engine, "beforeAll", testCase.beforeAllScript());
            logTab.inc();
            if (testCaseRunner.executeTestCase(engine, testCase, xUnitSuite)) {
                finalFailureOrError = true;
            }
            logTab.dec();
            testCaseRunner.evalScript(engine, "afterAll", testCase.afterAllScript());
            xUnitSuite.end();
        }
        xUnitReport.end();
        logOptions(testCases, env, tags);
        TestCaseRunner.logSynthesis(xUnitReport);
        try {
            Files.writeString(Path.of(report), xUnitReport.toXml());
            Matrix.save(matrix, env, tags, xUnitReport);
        } catch (final IOException e) {
            throw new KTestException("Failed to write test report.", e);
        }
        engine.end();
        if (finalFailureOrError) {
            throw new TestFailureOrError(xUnitReport);
        }
    }
}
