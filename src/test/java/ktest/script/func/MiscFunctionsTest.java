package ktest.script.func;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.script.Engine;
import ktest.script.ScriptException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MiscFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void unknownFunctionTest() {
        try {
            engine.eval("plouf()");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: unknown function in >>>plouf<<<", e.getMessage());
        }
    }

    @Test
    void invalidNumberOfArg() {
        try {
            engine.eval("uuid(1)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid number of arguments in uuid: 0 expected, 1 found.", e.getMessage());
        }
    }

    @Test
    void invalidTypeOfArg() {
        try {
            engine.eval("regexgen(1)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid type of argument in regexgen: class java.lang.String expected, class java.lang.Long found.", e.getMessage());
        }
    }

    @Test
    void pauseTest() {
        var before = System.currentTimeMillis();
        engine.eval("pause(2000)");
        assertTrue(System.currentTimeMillis() - before >= 2000);

        before = System.currentTimeMillis();
        engine.context().disablePause(true);
        engine.eval("pause(5000)");
        assertTrue(System.currentTimeMillis() - before <= 1000);
    }

    @Test
    void envTest() {
        assertEquals(System.getenv("JAVA_HOME"), engine.eval("env(\"JAVA_HOME\")"));
        assertEquals("", engine.eval("env(\"DoesNotExistEnvVariable\")"));
    }
}
