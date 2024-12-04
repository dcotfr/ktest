package ktest;

import com.google.common.base.Strings;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.core.KTestException;
import ktest.domain.TestCase;
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
import static ktest.core.AnsiColor.LIGHTGRAY;
import static ktest.core.AnsiColor.WHITE;
import static ktest.core.LogTab.secondsToHuman;

@CommandLine.Command(name = "prun", description = "Parallel run of test case(s).",
        mixinStandardHelpOptions = true, showDefaultValues = true, version = VERSION)
public class PRunCommand implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PRunCommand.class);

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
    private final Instance<TestCaseRunner> testCaseRunnerFactory;

    @Inject
    PRunCommand(final Instance<Engine> pEngineFactory, final Instance<TestCaseRunner> pTestCaseRunnerFactory) {
        engineFactory = pEngineFactory;
        testCaseRunnerFactory = pTestCaseRunnerFactory;
    }

    @Override
    public void run() {
        final var testCases = TestCase.load(file);
        if (!Strings.isNullOrEmpty(tags)) {
            LOG.info("{}Filtering Test Cases by: {}", WHITE, tags);
        }
        final var globalEngine = engineFactory.get();
        final var globalVariables = globalEngine.reset().context().variables();
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            LOG.info("Start parallel run.");
            final var subTasks = new ArrayList<StructuredTaskScope.Subtask<XUnitReport>>();
            filteredByTags(testCases, tags)
                    .stream()
                    .map(testCase -> testCaseRunnerFactory.get().testCase(testCase).backOffset(backOffset))
                    .forEach(runner -> {
                        runner.engine().init(globalVariables);
                        subTasks.add(scope.fork(runner));
                    });
            scope.join();
            LOG.info("End parallel run.");
            logOptions(testCases, env, tags);
            final var finalReport = new XUnitReport(subTasks.stream().map(StructuredTaskScope.Subtask::get).toList());
            logTips(finalReport);
            TestCaseRunner.logSynthesis(finalReport);
            try {
                Files.writeString(Path.of(report), finalReport.toXml());
                Matrix.save(Path.of(matrix), env, tags, finalReport);
            } catch (final IOException e) {
                throw new KTestException("Failed to write test report.", e);
            }
            globalEngine.end();
            if (finalReport.errors() > 0 || finalReport.failures() > 0) {
                throw new TestFailureOrError(finalReport);
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void logTips(final XUnitReport pReport) {
        final var parallelTime = pReport.time();
        final var sequentialTime = pReport.testsuite.stream().mapToDouble(XUnitSuite::time).sum();
        final var estimatedGain = round(1_000 - 1_000 * parallelTime / (sequentialTime != 0.0 ? sequentialTime : 1.0)) / 10.0;
        LOG.debug("{}Executed in {} (vs estimated sequential run time {} = gain {}%)", LIGHTGRAY, secondsToHuman(parallelTime), secondsToHuman(sequentialTime), estimatedGain);
        if (estimatedGain < 33.3) {
            LOG.debug("{}Tips: although slightly slower, the sequential mode offers a more readable log.", LIGHTGRAY);
        }
    }
}
