package ktest.script.func.log;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.script.Engine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class LogFunctionsTest {
    private final Engine engine;

    @Inject
    LogFunctionsTest(final Engine pEngine) {
        engine = pEngine;
    }

    @Test
    void debugTest() {
        assertEquals("LoggedText", engine.eval("debug(concat(\"Logged\",\"Text\"))"));
    }

    @Test
    void errorTest() {
        assertEquals("@#$!!!", engine.eval("error(\"@#$!!!\")"));
    }

    @Test
    void infoTest() {
        assertEquals("5", engine.eval("info(2+3)"));
        assertEquals("", engine.eval("info(\"\")"));
        assertEquals("To be displayed", engine.eval("info(\"To be displayed\")"));
    }

    @Test
    void traceTest() {
        assertEquals("2.5", engine.eval("trace(5/2)"));
    }

    @Test
    void warnTest() {
        assertEquals("Warning!", engine.eval("warn(\"Warning!\")"));
    }

    @Test
    void unboundParamsTest() {
        assertEquals("12", engine.eval("trace(1, 2)"));
        assertEquals("136", engine.eval("debug(1, 1+2, 1+2+3)"));
        assertEquals("Result = 6", engine.eval("info(\"Result = \", 2*3)"));
        assertEquals("To be displayed", engine.eval("warn(\"To\", \" \", \"be\", \" \", \"displayed\")"));
        assertEquals("", engine.eval("error()"));
    }
}
