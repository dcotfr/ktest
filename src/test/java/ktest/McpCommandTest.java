package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusMainTest
class McpCommandTest {
    static final String OPTIONS = String.join(System.lineSeparator(),
            "I   -c, --config=<config>   Path of the config file.",
            "I                             Default: ktconfig.yml",
            "I   -h, --help              Show this help message and exit.",
            "I   -V, --version           Print version information and exit.");

    @Test
    @Launch({"mcp", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest mcp [-hV] [-c=<config>]",
                "I Start as MCP tool server.",
                OPTIONS);
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch({"mcp", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.34", pResult.getOutput());
    }
}
