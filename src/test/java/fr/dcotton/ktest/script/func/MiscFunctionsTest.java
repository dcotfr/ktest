package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Engine;
import fr.dcotton.ktest.script.ScriptException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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
        final var before = System.currentTimeMillis();
        engine.eval("pause(2000)");
        final var after = System.currentTimeMillis();
        assertTrue(after - before >= 2000);
    }

    @Test
    void envTest() {
        assertEquals(System.getenv("JAVA_HOME"), engine.eval("env(\"JAVA_HOME\")"));
        assertEquals("", engine.eval("env(\"DoesNotExistEnvVariable\")"));
    }
}
