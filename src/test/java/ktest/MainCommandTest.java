package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusMainTest
class MainCommandTest {
    @Test
    @Launch(value = {"-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest [-hV] [COMMAND]",
                "I Kafka test utility.",
                "I   -h, --help      Show this help message and exit.",
                "I   -V, --version   Print version information and exit.",
                "I Commands:",
                "I   srun  Sequential run of test case(s).",
                "I   prun  Parallel run of test case(s).",
                "I   doc   Display full documentation.\r");
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.0\r", pResult.getOutput());
    }
}
