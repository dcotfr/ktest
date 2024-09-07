package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusMainTest
class PRunCommandTest {
    @Test
    @Launch(value = {"prun", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest prun [-hV] [-b=<backOffset>] [-c=<config>] -e=<env> [-f=<file>]",
                "I                   [-r=<report>]",
                "I Parallel run of test case(s).",
                "I   -b, --back=<backOffset>   Back offset.",
                "I                               Default: 250",
                "I   -c, --config=<config>     Path of the config file.",
                "I                               Default: ktconfig.yml",
                "I   -e, --env=<env>           Name of the environment to use.",
                "I   -f, --file=<file>         Path of test case description file to execute.",
                "I                               Default: ktestcase.yml",
                "I   -h, --help                Show this help message and exit.",
                "I   -r, --report=<report>     Path of the test report file.",
                "I                               Default: ktreport.xml",
                "I   -V, --version             Print version information and exit.\r");
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"prun", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.0\r", pResult.getOutput());
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
}
