package fr.dcotton.ktest.script.func.log;

import fr.dcotton.ktest.script.Engine;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class LogFunctionsTest {
    @Inject
    private Engine engine;

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
        assertEquals(5L, engine.eval("info(2+3)"));
        assertEquals("", engine.eval("info(\"\")"));
        assertEquals("To be displayed", engine.eval("info(\"To be displayed\")"));
    }

    @Test
    void traceTest() {
        assertEquals(2.5, engine.eval("trace(5/2)"));
    }

    @Test
    void warnTest() {
        assertEquals("Warning!", engine.eval("warn(\"Warning!\")"));
    }
}
