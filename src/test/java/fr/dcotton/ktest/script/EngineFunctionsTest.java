package fr.dcotton.ktest.script;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EngineFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void fakerRegexTest() {
        assertEquals("AAAAAAAA", engine.eval("faker.regex(\"A{8}\")"));
    }

    @Test
    void fakerUuidTest() {
        final var pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertTrue(pattern.matcher(engine.eval("faker.uuid()").toString()).matches());
    }

    @Test
    void timeFormatTest() {
        assertEquals("2024-02-25 10:53:41", engine.eval("time.format(\"yyyy-MM-dd HH:mm:ss\", 1708854821321)"));
    }

    @Test
    void invalidTimeFormatTest() {
        try {
            engine.eval("time.format(\"aaaa-MM-jj\", 1708854821321)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid date/time format in time.format: aaaa-MM-jj", e.getMessage());
        }
    }

    @Test
    void timeNowTest() {
        final var before = System.currentTimeMillis();
        final var t = (Long) engine.eval("time.now()");
        final var after = System.currentTimeMillis();
        assertTrue(before <= t && t <= after);
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
            engine.eval("faker.uuid(1)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid number of arguments in faker.uuid: 0 expected, 1 found.", e.getMessage());
        }
    }

    @Test
    void invalidTypeOfArg() {
        try {
            engine.eval("faker.regex(1)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid type of argument in faker.regex: class java.lang.String expected, class java.lang.Long found.", e.getMessage());
        }
    }

    @Test
    void pauseTest() {
        final var before = System.currentTimeMillis();
        engine.eval("pause(2000)");
        final var after = System.currentTimeMillis();
        assertTrue(after - before >= 2000);
    }
}
