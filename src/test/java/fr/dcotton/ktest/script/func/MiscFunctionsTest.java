package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Engine;
import fr.dcotton.ktest.script.ScriptException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MiscFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void regexgenTest() {
        assertEquals("AAAAAAAA", engine.eval("regexgen(\"A{8}\")"));
    }

    @Test
    void uuidTest() {
        final var pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertTrue(pattern.matcher(engine.eval("uuid()").toString()).matches());
    }

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
    void printTest() {
        assertEquals(5L, engine.eval("print(2+3)"));
        assertEquals("", engine.eval("print(\"\")"));
        assertEquals("To be displayed", engine.eval("print(\"To be displayed\")"));
    }

    @Test
    void envTest() {
        assertEquals(System.getenv("JAVA_HOME"), engine.eval("env(\"JAVA_HOME\")"));
        assertEquals("", engine.eval("env(\"DoesNotExistEnvVariable\")"));
    }
}
