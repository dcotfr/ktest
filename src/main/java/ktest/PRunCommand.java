package ktest;

import com.google.common.base.Strings;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.domain.TestCase;
import ktest.domain.config.EnvironmentConfig;
import ktest.domain.config.KTestConfig;
import ktest.domain.xlsx.Matrix;
import ktest.domain.xunit.XUnitReport;
import ktest.domain.xunit.XUnitSuite;
import ktest.script.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.StructuredTaskScope;

import static java.lang.Math.round;
import static ktest.MainCommand.VERSION;
import static ktest.TestCaseRunner.filteredByTags;
import static ktest.TestCaseRunner.logOptions;
import static ktest.core.AnsiColor.WHITE;
import static ktest.core.LogTab.secondsToHuman;

@CommandLine.Command(name = "prun", description = "Parallel run of test case(s).",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class PRunCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PRunCommand.class);

    @CommandLine.Mixin
    private Options cliOptions;

    private final Instance<KTestConfig> configFactory;
    private final Instance<Engine> engineFactory;
    private final Instance<TestCaseRunner> testCaseRunnerFactory;

    @Inject
    PRunCommand(final Instance<KTestConfig> pConfigFactory, final Instance<Engine> pEngineFactory, final Instance<TestCaseRunner> pTestCaseRunnerFactory) {
        configFactory = pConfigFactory;
        engineFactory = pEngineFactory;
        testCaseRunnerFactory = pTestCaseRunnerFactory;
    }

    @Override
    public void run() {
        final var testCases = TestCase.load(cliOptions.file);
        final var currentEnv = configFactory.get().currentEnvironment();
        final var actualTags = currentEnv.actualTags(cliOptions);
        if (!Strings.isNullOrEmpty(actualTags)) {
            LOG.info("{}Filtering Test Cases by: {}", WHITE, actualTags);
        }
        final var globalEngine = engineFactory.get();
        final var globalVariables = globalEngine.reset().context().variables();
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            LOG.info("Start parallel run.");
            final var subTasks = new ArrayList<StructuredTaskScope.Subtask<XUnitReport>>();
            filteredByTags(testCases, actualTags)
                    .stream()
                    .map(testCase -> testCaseRunnerFactory.get().testCase(testCase).backOffset(currentEnv.actualBackOffset(cliOptions)))
                    .forEach(runner -> {
                        runner.engine().init(globalVariables);
                        subTasks.add(scope.fork(runner));
                    });
            scope.join();
            LOG.info("End parallel run.");
            logOptions(testCases, cliOptions.env, actualTags);
            final var finalReport = new XUnitReport(subTasks.stream().map(StructuredTaskScope.Subtask::get).toList());
            logTips(finalReport);
            TestCaseRunner.logSynthesis(finalReport);
            saveReports(finalReport, currentEnv);
            globalEngine.end();
            if (finalReport.errors() > 0 || finalReport.failures() > 0) {
                throw new TestFailureOrError(finalReport);
            }
        } catch (final InterruptedException e) {
            LOG.warn("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
    }

    private static void logTips(final XUnitReport pReport) {
        final var parallelTime = pReport.time();
        final var sequentialTime = pReport.testsuite.stream().mapToDouble(XUnitSuite::time).sum();
        final var estimatedGain = round(1_000 - 1_000 * parallelTime / (sequentialTime != 0.0 ? sequentialTime : 1.0)) / 10.0;
        LOG.info("Executed in {} (vs estimated sequential run time {} = gain {}%)", secondsToHuman(parallelTime), secondsToHuman(sequentialTime), estimatedGain);
        if (estimatedGain < 33.3) {
            LOG.info("Tips: although slightly slower, the sequential mode offers a more readable log.");
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
