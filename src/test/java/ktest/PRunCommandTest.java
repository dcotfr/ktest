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
class PRunCommandTest {
    @Test
    @Launch(value = {"prun", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest prun [-hV] [-b=<backOffset>] [-c=<config>] -e=<env> [-f=<file>]",
                "I                   [-m=<matrix>] [-r=<report>] [-t=<tags>]",
                "I Parallel run of test case(s).",
                SRunCommandTest.OPTIONS);
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"prun", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.12\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"prun", "-e=dev", "-f=</->>"}, exitCode = 1)
    void invalidFilePathTest(final LaunchResult pResult) {
        assertEquals("E Failed to read test case file </->>\r", pResult.getOutputStream().getFirst());
    }

    @Test
    @Launch(value = {"prun", "-e=dev", "-f=unknownFile.yml"}, exitCode = 1)
    void fileNotFoundTest(final LaunchResult pResult) {
        assertEquals("E Failed to read test case file C:\\Users\\David\\IdeaProjects\\ktest\\unknownFile.yml\r", pResult.getOutputStream().getFirst());
    }

    @Test
    @Launch(value = {"prun", "-e=pi", "-f=src\\test\\resources\\validFile.yml", "-t=tag2"})
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
    @Launch(value = {"srun", "-e=pi", "-f=src\\test\\resources\\gotoFile.yml"})
    void gotoFileTest(final LaunchResult pResult) {
        final int found = (int) pResult.getOutputStream().stream()
                .filter(log -> log.equals("I   - Step : Step n°1 (SEND)\r"))
                .count();
        assertEquals(5, found);
    }
}
