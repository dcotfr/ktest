package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import ktest.domain.Action;
import ktest.domain.TestCase;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusMainTest
class SRunCommandTest {
    static final String OPTIONS = String.join(System.lineSeparator(),
            "I   -b, --back=<backOffset>   Back offset.",
            "I                               Default: 250",
            "I   -c, --config=<config>     Path of the config file.",
            "I                               Default: ktconfig.yml",
            "I   -e, --env=<env>           Name of the environment to use.",
            "I   -f, --file=<file>         Path of test case description file to execute.",
            "I                               Default: ktestcase.yml",
            "I   -h, --help                Show this help message and exit.",
            "I   -m, --matrix=<matrix>     Path of the matrix summary file (xlsx format).",
            "I                               Default: ktmatrix.xlsx",
            "I   -p, --pause=<autoPause>   Delay of auto pause before first PRESENT/ABSENT",
            "I                               following SEND (0 for no pause).",
            "I                               Default: 0",
            "I   -r, --report=<report>     Path of the test report file (JUnit format).",
            "I                               Default: ktreport.xml",
            "I   -t, --tags=<tags>         Tags to filter test cases to run.",
            "I   -V, --version             Print version information and exit.\r");

    @Test
    @Launch(value = {"srun", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest srun [-hV] [-b=<backOffset>] [-c=<config>] -e=<env> [-f=<file>]",
                "I                   [-m=<matrix>] [-p=<autoPause>] [-r=<report>] [-t=<tags>]",
                "I Sequential run of test case(s).",
                OPTIONS);
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"srun", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.21\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"srun", "-e=dev", "-f=</->>"}, exitCode = 1)
    void invalidFilePathTest(final LaunchResult pResult) {
        assertEquals("E Failed to read test case file </->>\r", pResult.getOutputStream().getFirst());
    }

    @Test
    @Launch(value = {"srun", "-e=dev", "-f=unknownFile.yml"}, exitCode = 1)
    void fileNotFoundTest(final LaunchResult pResult) {
        assertEquals("E Failed to read test case file C:\\Users\\David\\IdeaProjects\\ktest\\unknownFile.yml\r", pResult.getOutputStream().getFirst());
    }

    @Test
    @Launch(value = {"srun", "-e=pi", "-f=src\\test\\resources\\validFile.yml"}, exitCode = 1)
    void validFileTest(final LaunchResult pResult) {
        final var testCases = TestCase.load("src\\test\\resources\\validFile.yml");
        assertEquals(4, testCases.size());
        final var testCase = testCases.getFirst();
        assertEquals("Test Case 1", testCase.name());
        assertEquals(List.of("BASE_TIMESTAMP = now()", "STEP1_1_CID = uuid()"),
                testCase.beforeAllScript());
        assertEquals(Collections.emptyList(), testCase.afterAllScript());

        final var steps = testCase.steps();
        assertEquals(4, steps.size());

        var step = steps.getFirst();
        assertEquals("Step n°1.1", step.name());
        assertEquals(List.of("TIMESTAMP = BASE_TIMESTAMP + 1000"), step.beforeScript());
        assertEquals("${KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${KAFKA_PREFIX}InputTopic", step.topic());
        assertEquals(Action.SEND, step.action());
        assertEquals(List.of("pause(100)"), step.afterScript());

        var rec = step.record();
        assertNull(rec.timestamp());
        final var headers = rec.headers();
        assertEquals(2, headers.size());
        assertEquals("${STEP1_1_CID}", headers.get("correlation.id"));
        assertEquals("machin", headers.get("truc"));
        assertEquals("{\"code\":\"P1\",\"label\":\"Product 1\"}", rec.keyNode().toString());
        assertEquals("{\"sender\":\"Source\",\"eventType\":\"CREATE\",\"eventTsp\":\"${TIMESTAMP}\","
                + "\"body\":{\"code\":\"P1\",\"label\":\"Product 1\",\"commandAt\":\"${BASE_TIMESTAMP}\","
                + "\"sentAt\":\"${BASE_TIMESTAMP + 100}\",\"weight\":12030.5}}", rec.valueNode().toString());

        step = steps.get(1);
        assertEquals("Step n°1.2", step.name());
        assertEquals(Collections.emptyList(), step.beforeScript());
        assertEquals("${KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${KAFKA_PREFIX}InputTopic", step.topic());
        assertEquals(Action.ABSENT, step.action());
        assertEquals(Collections.emptyList(), step.afterScript());

        rec = step.record();
        assertEquals(123456789L, rec.longTimestamp());
        assertTrue(rec.headers().isEmpty());
        assertEquals("\"P1\"", rec.keyNode().toString());
        assertEquals("{\"code\":\"P1\",\"count\":1}", rec.valueNode().toString());

        step = steps.get(2);
        assertEquals("Unnamed", step.name());
        assertEquals(Collections.emptyList(), step.beforeScript());
        assertEquals("${KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${KAFKA_PREFIX}InputTopic", step.topic());
        assertEquals(Action.PRESENT, step.action());
        assertEquals(Collections.emptyList(), step.afterScript());

        rec = step.record();
        assertNull(rec.timestamp());
        assertTrue(rec.headers().isEmpty());
        assertEquals("{\"code\":\"P1\"}", rec.keyNode().toString());
        assertEquals("{\"sender\":\"Source\",\"eventTsp\":\"${TIMESTAMP}\",\"body\":{\"code\":\"P1\",\"label\":\"Product 1\"}}", rec.valueNode().toString());

        step = steps.get(3);
        assertEquals("Last Step", step.name());
        assertEquals(Collections.emptyList(), step.beforeScript());
        assertEquals("${KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${KAFKA_PREFIX}InputTopic2", step.topic());
        assertEquals(Action.TODO, step.action());
        assertEquals(Collections.emptyList(), step.afterScript());

        rec = step.record();
        assertNull(rec.timestamp());
        assertTrue(rec.headers().isEmpty());
        assertEquals("\"K1\"", rec.keyNode().toString());
        assertNull(rec.value());
    }

    @Test
    @Launch(value = {"srun", "-e=piTag", "-f=src\\test\\resources\\validFile.yml"})
    void validFileTagTest(final LaunchResult pResult) {
        final int found = (int) pResult.getOutputStream().stream()
                .filter(log -> log.startsWith("I Test Case: "))
                .count();
        assertEquals(2, found);
    }

    @Test
    @Launch(value = {"srun", "-e=pi", "-f=src\\test\\resources\\gotoFile.yml"})
    void gotoFileTest(final LaunchResult pResult) {
        final int found = (int) pResult.getOutputStream().stream()
                .filter(log -> log.equals("I   - Step : Step n°1 (SEND)\r"))
                .count();
        assertEquals(5, found);
    }

    @Test
    @Launch(value = {"srun", "-e=piTag", "-p=10", "-f=src\\test\\resources\\validFile.yml"})
    void validFileAutoPauseTest(final LaunchResult pResult) {
        final int found = (int) pResult.getOutputStream().stream()
                .filter(log -> log.startsWith("D     Auto pause 10ms before assert..."))
                .count();
        assertEquals(2, found);
    }
}
