package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusMainTest
class ScanCommandTest {
    static final String OPTIONS = String.join(System.lineSeparator(),
            "I   -c, --config=<config>   Path of the config file.",
            "I                             Default: ktconfig.yml",
            "I   -e, --env=<env>         Name of the environment to use.",
            "I   -h, --help              Show this help message and exit.",
            "I   -i, --inputs=<inputs>   List of 'topic@broker,...' to scan.",
            "I   -V, --version           Print version information and exit.\r");

    @Test
    @Launch(value = {"scan", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest scan [-hV] [-c=<config>] -e=<env> -i=<inputs>",
                "I Scan topic(s) to extract a sample test case.",
                OPTIONS);
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"scan", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.16\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"scan", "-e=dev", "-i=topic1,topic2"}, exitCode = 1)
    void invalidInputs(final LaunchResult pResult) {
        assertEquals("E Malformated inputs: topic1,topic2\r", pResult.getOutputStream().get(3));
    }

    @Test
    @Launch(value = {"scan", "-e=pi", "-i=InputTopic@pi_broker,InputTopicStr@pi_broker"})
    void validTest(final LaunchResult pResult) {
    }
}
