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

    @Test
    void hashMd5Test() {
        assertEquals("ec1dd92925cb06934c047fb3f5380cba", engine.eval("hash.md5(\"SampleString\")"));
    }

    @Test
    void hashSha256Test() {
        assertEquals("77b12c9c6213a05fb60cd8151c51ca522e7ebd8e55096a6a8b2c34769ec4fc20", engine.eval("hash.sha256(\"SampleString\")"));
    }

    @Test
    void hashSha512Test() {
        assertEquals("aee8e20df4b3ce730e2e4f04ca8a1becb522d769559c0791a04a4d18745caa8c6eb43c48ce265f1ceca33c65e789faace6e75bfa40dab0ece7e03c6fcda75961", engine.eval("hash.sha512(\"SampleString\")"));
    }

    @Test
    void base64DecodeTest() {
        assertEquals("Text", engine.eval("base64.decode(\"VGV4dA==\")"));
    }

    @Test
    void invalidBase64DecodeTest() {
        try {
            engine.eval("base64.decode(\"!InvalidString!\")");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid base64 string in base64.decode: !InvalidString!", e.getMessage());
        }
    }


    @Test
    void base64EncodeTest() {
        assertEquals("U2FtcGxlU3RyaW5n", engine.eval("base64.encode(\"SampleString\")"));
        assertEquals("", engine.eval("base64.encode(\"\")"));
    }

    @Test
    void envTest() {
        assertEquals(System.getenv("JAVA_HOME"), engine.eval("env(\"JAVA_HOME\")"));
        assertEquals("", engine.eval("env(\"DoesNotExistEnvVariable\")"));
    }

    @Test
    void mathAbsTest() {
        assertEquals(3.14, engine.eval("math.abs(-3.14)"));
        assertEquals(5.4321, engine.eval("math.abs(5.4321)"));
        assertEquals(123L, engine.eval("math.abs(-123)"));
        assertEquals(321L, engine.eval("math.abs(321)"));
    }

    @Test
    void mathCeilTest() {
        assertEquals(4L, engine.eval("math.ceil(3.14)"));
    }

    @Test
    void mathFloorTest() {
        assertEquals(3L, engine.eval("math.floor(3.14)"));
    }
}
