package fr.dcotton.ktest;

import fr.dcotton.ktest.domain.Action;
import fr.dcotton.ktest.domain.TestCase;
import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusMainTest
class RunCommandTest {
    @Test
    @Launch(value = {"run", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest run [-hV] [-c=<config>] -e=<env> [-f=<file>] [-r=<report>]",
                "I Run test case.",
                "I   -c, --config=<config>   Path of the config file.",
                "I                             Default: ktconfig.yml",
                "I   -e, --env=<env>         Name of the environment to use.",
                "I   -f, --file=<file>       Path of test case description file to execute.",
                "I                             Default: ktestcase.yml",
                "I   -h, --help              Show this help message and exit.",
                "I   -r, --report=<report>   Path of the test report file.",
                "I                             Default: ktreport.xml",
                "I   -V, --version           Print version information and exit.\r");
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"run", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.0\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"run", "-e=dev", "-f=</->>"}, exitCode = 1)
    void invalidFilePathTest(final LaunchResult pResult) {
        assertEquals("E Failed to read test case file </->>\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"run", "-e=dev", "-f=unknownFile.yml"}, exitCode = 1)
    void fileNotFoundTest(final LaunchResult pResult) {
        assertEquals("E Failed to read test case file C:\\Users\\David\\IdeaProjects\\ktest\\unknownFile.yml\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"run", "-e=pi", "-f=src\\test\\resources\\validFile.yml"})
    void validFileTest(final LaunchResult pResult) {
        final var testCases = TestCase.load("src\\test\\resources\\validFile.yml");
        assertEquals(2, testCases.size());
        final var testCase = testCases.getFirst();
        assertEquals("Test Case 1", testCase.name());
        assertEquals(List.of("BASE_TIMESTAMP = time.now()", "STEP1_CID = faker.uuid()", "STEP2_CID = faker.uuid()"),
                testCase.beforeAllScript());
        assertEquals(Collections.emptyList(), testCase.afterAllScript());

        final var steps = testCase.steps();
        assertEquals(4, steps.size());

        var step = steps.getFirst();
        assertEquals("Step n°1", step.name());
        assertEquals(List.of("TIMESTAMP = BASE_TIMESTAMP + 1000"), step.beforeScript());
        assertEquals("${TNR_KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${TNR_KAFKA_LOCAL_PREFIX}InputTopic", step.topic());
        assertEquals(Action.SEND, step.action());
        assertEquals(List.of("pause(100)"), step.afterScript());

        var rec = step.record();
        assertNull(rec.timestamp());
        final var headers = rec.headers();
        assertEquals(1, headers.size());
        assertEquals("${STEP1_CID}", headers.get("correlation.id"));
        assertEquals("{\"code\":\"P1\",\"label\":\"Product 1\"}", rec.keyNode().toString());
        assertEquals("{\"sender\":\"Source\",\"eventType\":\"CREATE\",\"eventTsp\":\"${TIMESTAMP}\","
                + "\"body\":{\"code\":\"P1\",\"label\":\"Product 1\",\"commandAt\":\"${BASE_TIMESTAMP}\","
                + "\"sentAt\":\"${BASE_TIMESTAMP + 100}\",\"weight\":12030.5}}", rec.valueNode().toString());

        step = steps.get(1);
        assertEquals("Step n°2", step.name());
        assertEquals(Collections.emptyList(), step.beforeScript());
        assertEquals("${TNR_KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${TNR_KAFKA_LOCAL_PREFIX}InputTopic", step.topic());
        assertEquals(Action.ABSENT, step.action());
        assertEquals(Collections.emptyList(), step.afterScript());

        rec = step.record();
        assertEquals(123456789L, rec.timestamp());
        assertTrue(rec.headers().isEmpty());
        assertEquals("\"P1\"", rec.keyNode().toString());
        assertEquals("{\"code\":\"P1\",\"count\":1}", rec.valueNode().toString());

        step = steps.get(2);
        assertEquals("Unnamed", step.name());
        assertEquals(Collections.emptyList(), step.beforeScript());
        assertEquals("${TNR_KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${TNR_KAFKA_LOCAL_PREFIX}InputTopic", step.topic());
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
        assertEquals("${TNR_KAFKA_LOCAL_CONTEXT}", step.broker());
        assertEquals("${TNR_KAFKA_LOCAL_PREFIX}InputTopic", step.topic());
        assertEquals(Action.TODO, step.action());
        assertEquals(Collections.emptyList(), step.afterScript());

        rec = step.record();
        assertNull(rec.timestamp());
        assertTrue(rec.headers().isEmpty());
        assertEquals("\"K1\"", rec.keyNode().toString());
        assertNull(rec.value());
    }
}
