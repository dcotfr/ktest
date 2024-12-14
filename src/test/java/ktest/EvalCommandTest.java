package ktest;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusMainTest
class EvalCommandTest {
    @Test
    @Launch(value = {"eval", "-h"})
    void helpOptionTest(final LaunchResult pResult) {
        final var expected = String.join(System.lineSeparator(),
                "I Usage: ktest eval [-hV] -l=<line>",
                "I Evaluates a script and displays its final result.",
                "I   -h, --help          Show this help message and exit.",
                "I   -l, --line=<line>   In-line statement(s) to evaluate.",
                "I   -V, --version       Print version information and exit.\r");
        assertEquals(expected, pResult.getOutput());
    }

    @Test
    @Launch(value = {"eval", "-V"})
    void versionOptionTest(final LaunchResult pResult) {
        assertEquals("I ktest v1.0.16\r", pResult.getOutput());
    }

    @Test
    @Launch(value = {"eval", "-l=a=2;b=3;a*b"})
    void simpleInlineTest(final LaunchResult pResult) {
        final var res = pResult.getOutputStream();
        assertEquals("D InLine: a=2;b=3;a*b\r", res.getFirst());
        assertEquals("I Result: 6\r", pResult.getOutputStream().get(1));
    }

    @Test
    @Launch(value = {"eval", "-l=\" a=2; b=3; a*b \""})
    void spacedInlineTest(final LaunchResult pResult) {
        final var res = pResult.getOutputStream();
        assertEquals("D InLine: a=2; b=3; a*b\r", res.getFirst());
        assertEquals("I Result: 6\r", res.get(1));
    }

    @Test
    @Launch(value = {"eval", "-l=key=aeskey();info(\"key=\",key);encrypted=aesenc(\"Clear Text\",key);info(\"encrypted=\", encrypted);aesdec(encrypted,key)"})
    void complexInlineTest(final LaunchResult pResult) {
        final var res = pResult.getOutputStream();
        assertEquals("D InLine: key=aeskey();info(\"key=\",key);encrypted=aesenc(\"Clear Text\",key);info(\"encrypted=\", encrypted);aesdec(encrypted,key)\r", res.getFirst());
        assertTrue(res.get(1).startsWith("I key="));
        assertTrue(res.get(2).startsWith("I encrypted="));
        assertEquals("I Result: Clear Text\r", res.get(3));
    }
}
