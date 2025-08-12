package ktest;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.Strings;
import ktest.domain.TestCase;
import ktest.domain.config.KTestConfig;
import ktest.domain.xunit.XUnitReport;
import ktest.domain.xunit.XUnitSuite;
import ktest.script.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.util.concurrent.StructuredTaskScope;

import static java.lang.Math.round;
import static java.util.concurrent.StructuredTaskScope.Subtask.State.SUCCESS;
import static ktest.MainCommand.VERSION;
import static ktest.SRunCommand.saveReports;
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
        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            LOG.info("Start parallel run.");
            final var parallelState = new ParallelState();
            filteredByTags(testCases, actualTags)
                    .stream()
                    .map(testCase -> testCaseRunnerFactory.get().testCase(testCase)
                            .autoPause(currentEnv.actualAutoPause(cliOptions)).backOffset(currentEnv.actualBackOffset(cliOptions))
                            .parallelState(parallelState))
                    .forEach(runner -> {
                        runner.engine().init(globalVariables);
                        parallelState.subTasks.add(scope.fork(runner));
                    });
            scope.join();
            LOG.info("End parallel run.");
            logOptions(testCases, cliOptions.env, actualTags);
            final var finalReport = new XUnitReport(parallelState.subTasks.stream()
                    .filter(subTask -> subTask.state() == SUCCESS)
                    .map(StructuredTaskScope.Subtask::get).toList());
            logTips(finalReport);
            TestCaseRunner.logSynthesis(finalReport);
            saveReports(cliOptions, finalReport, currentEnv);
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
}
