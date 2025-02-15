package ktest;

import com.google.common.base.Strings;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.core.LogTab;
import ktest.domain.TestCase;
import ktest.domain.config.EnvironmentConfig;
import ktest.domain.config.KTestConfig;
import ktest.domain.xlsx.Matrix;
import ktest.domain.xunit.XUnitReport;
import ktest.domain.xunit.XUnitSuite;
import ktest.kafka.ClusterClient;
import ktest.script.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.Math.round;
import static ktest.MainCommand.VERSION;
import static ktest.TestCaseRunner.filteredByTags;
import static ktest.TestCaseRunner.logOptions;
import static ktest.core.AnsiColor.WHITE;
import static ktest.core.LogTab.secondsToHuman;

@CommandLine.Command(name = "srun", description = "Sequential run of test case(s).",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class SRunCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(SRunCommand.class);

    @CommandLine.Mixin
    private Options cliOptions;

    private final Instance<KTestConfig> configFactory;
    private final Instance<Engine> engineFactory;
    private final Instance<ClusterClient> kafkaClientFactory;
    private final LogTab logTab = new LogTab(false);

    @Inject
    SRunCommand(final Instance<KTestConfig> pConfigFactory, final Instance<Engine> pEngineFactory, final Instance<ClusterClient> pKafkaClientFactory) {
        configFactory = pConfigFactory;
        engineFactory = pEngineFactory;
        kafkaClientFactory = pKafkaClientFactory;
    }

    @Override
    public void run() {
        final var engine = engineFactory.get();
        final var testCases = TestCase.load(cliOptions.file);
        final var currentEnv = configFactory.get().currentEnvironment();
        final var testCaseRunner = new TestCaseRunner(engine, kafkaClientFactory.get())
                .autoPause(currentEnv.actualAutoPause(cliOptions)).backOffset(currentEnv.actualBackOffset(cliOptions))
                .logTab(logTab);
        var finalFailureOrError = false;
        final var actualTags = currentEnv.actualTags(cliOptions);
        if (!Strings.isNullOrEmpty(actualTags)) {
            LOG.info("{}Filtering Test Cases by: {}", WHITE, actualTags);
        }
        final var xUnitReport = new XUnitReport();
        final var globalVariables = engine.reset().context().variables();
        for (final var testCase : filteredByTags(testCases, actualTags)) {
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
        logOptions(testCases, cliOptions.env, actualTags);
        logTips(xUnitReport);
        TestCaseRunner.logSynthesis(xUnitReport);
        saveReports(xUnitReport, currentEnv);
        engine.end();
        if (finalFailureOrError) {
            throw new TestFailureOrError(xUnitReport);
        }
    }

    private static void logTips(final XUnitReport pReport) {
        final var fullTime = pReport.time();
        final var maxSuiteTime = pReport.testsuite.stream().mapToDouble(XUnitSuite::time).filter(s -> s >= 0.0).max().orElse(0.0);
        final var potentialGain = round(1_000 - 1_000 * maxSuiteTime / (fullTime != 0.0 ? fullTime : 1.0)) / 10.0;
        if (potentialGain > 33.3) {
            LOG.info("Tips: potential speed gain with parallel mode = {}% (executed in {} with the slowest sequence requiring {}).", potentialGain, secondsToHuman(fullTime), secondsToHuman(maxSuiteTime));
        }
    }

    private void saveReports(final XUnitReport pXmlReport, final EnvironmentConfig pEnvConfig) {
        try {
            Files.writeString(Path.of(pEnvConfig.actualReport(cliOptions)), pXmlReport.toXml());
            Matrix.save(Path.of(pEnvConfig.actualMatrix(cliOptions)), cliOptions.env, pEnvConfig.actualTags(cliOptions), pXmlReport);
        } catch (final IOException e) {
            throw new KTestException("Failed to write test reports.", e);
        }
    }
}
