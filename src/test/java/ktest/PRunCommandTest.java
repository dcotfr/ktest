package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusMainTest
class PRunCommandTest {
    @Test
    @Launch(value = {"prun", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest prun [-hV] [-b=<backOffset>] [-c=<config>] -e=<env> [-f=<file>]",
                "I                   [-r=<report>]",
                "I Parallel run of test case(s).",
                SRunCommandTest.OPTIONS);
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"prun", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.3\r", pResult.getOutput());
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
    @Launch(value = {"prun", "-e=pi", "-f=src\\test\\resources\\validFile.yml"})
    void validFileTest(final LaunchResult pResult) {
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
