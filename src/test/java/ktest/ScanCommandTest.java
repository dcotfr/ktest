package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusMainTest
class ScanCommandTest {
    static final String OPTIONS = String.join(System.lineSeparator(),
            "I   -c, --config=<config>   Path of the config file.",
            "I                             Default: ktconfig.yml",
            "I   -e, --env=<env>         Name of the environment to use.",
            "I   -h, --help              Show this help message and exit.",
            "I   -i, --inputs=<inputs>   List of 'topic@broker,...' (or '@broker' ref) to scan.",
            "I   -o, --output=<output>   Path of output sample file.",
            "I                             Default: ktsample.yml",
            "I   -V, --version           Print version information and exit.\r");

    @Test
    @Launch(value = {"scan", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest scan [-hV] [-c=<config>] -e=<env> -i=<inputs> [-o=<output>]",
                "I Scan topic(s) to extract a sample test case.",
                OPTIONS);
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"scan", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.20\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"scan", "-e=dev", "-i=topic1,topic2"}, exitCode = 1)
    void invalidInputs(final LaunchResult pResult) {
        assertEquals("E Malformated inputs: topic1,topic2\r", pResult.getOutputStream().getFirst());
    }

    @Test
    @Launch(value = {"scan", "-e=dev", "-i=topic1@undefined"}, exitCode = 1)
    void unknownBroker(final LaunchResult pResult) {
        assertEquals("E Unknown broker 'undefined' in inputs: topic1@undefined\r", pResult.getOutputStream().getFirst());
    }

    @Test
    @Launch(value = {"scan", "-e=pi", "-i=InputTopic@pi_broker, InputTopicStr @ pi_broker,OutputTopic@pi_broker", "-o=ktsample-test.yml"})
    void validSelectTest(final LaunchResult pResult) throws IOException {
        assertEquals("I Scanning last record of InputTopic@pi_broker\r", pResult.getOutputStream().getFirst());
        assertEquals("I Scanning last record of InputTopicStr@pi_broker\r", pResult.getOutputStream().get(1));
        assertEquals("I Scanning last record of OutputTopic@pi_broker\r", pResult.getOutputStream().get(2));
        assertEquals("W No record found in OutputTopic@pi_broker\r", pResult.getOutputStream().get(3));
        assertEquals("I Sample test case created in ktsample-test.yml file.\r", pResult.getOutputStream().get(4));

        validateFile();
    }

    @Test
    @Launch(value = {"scan", "-e=pi", "-i=@pi_broker", "-o=ktsample-test.yml"})
    void validAutoTest(final LaunchResult pResult) throws IOException {
        assertEquals("I Auto scan broker 'pi_broker'...\r", pResult.getOutputStream().getFirst());
        assertEquals("I Scanning last record of CompactTopic@pi_broker\r", pResult.getOutputStream().get(1));
        assertEquals("W No record found in CompactTopic@pi_broker\r", pResult.getOutputStream().get(2));
        assertEquals("I Scanning last record of InputTopic@pi_broker\r", pResult.getOutputStream().get(3));
        assertEquals("I Scanning last record of InputTopicStr@pi_broker\r", pResult.getOutputStream().get(4));
        assertEquals("I Scanning last record of OutputTopic@pi_broker\r", pResult.getOutputStream().get(5));
        assertEquals("W No record found in OutputTopic@pi_broker\r", pResult.getOutputStream().get(6));
        assertEquals("I Scanning last record of OutputTopicStr@pi_broker\r", pResult.getOutputStream().get(7));
        assertEquals("W No record found in OutputTopicStr@pi_broker\r", pResult.getOutputStream().get(8));
        assertEquals("I Sample test case created in ktsample-test.yml file.\r", pResult.getOutputStream().get(9));

        validateFile();
    }

    private static void validateFile() throws IOException {
        final var testFile = Path.of("ktsample-test.yml");
        final var lines = Files.readAllLines(testFile, StandardCharsets.UTF_8);
        assertTrue(lines.remove("---"));
        assertTrue(lines.remove("name: Sample Test Case"));
        assertTrue(lines.remove("steps:"));
        assertTrue(lines.remove("  - name: Step n°1"));
        assertTrue(lines.remove("    broker: pi_broker"));
        assertTrue(lines.remove("    topic: InputTopic"));
        assertTrue(lines.remove("    keySerde: STRING"));
        assertTrue(lines.remove("    valueSerde: AVRO"));
        assertTrue(lines.remove("    action: TODO"));
        assertTrue(lines.remove("    record:"));
        assertTrue(lines.remove("      headers:"));
        assertTrue(lines.remove("  - name: Step n°2"));
        assertTrue(lines.remove("    broker: pi_broker"));
        assertTrue(lines.remove("    topic: InputTopicStr"));
        assertTrue(lines.remove("    keySerde: STRING"));
        assertTrue(lines.remove("    valueSerde: STRING"));
        assertTrue(lines.remove("    action: TODO"));
        assertTrue(lines.remove("    record:"));
        assertFalse(lines.remove("      headers:"));
        Files.delete(testFile);
    }
}
