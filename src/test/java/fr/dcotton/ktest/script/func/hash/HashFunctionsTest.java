package fr.dcotton.ktest.script.func.hash;

import fr.dcotton.ktest.script.Engine;
import fr.dcotton.ktest.script.ScriptException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class HashFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void crc32Test() {
        assertEquals("3ca8bf4", engine.eval("crc32(\"SampleString\")"));
    }

    @Test
    void md5Test() {
        assertEquals("ec1dd92925cb06934c047fb3f5380cba", engine.eval("md5(\"SampleString\")"));
    }

    @Test
    void sha1Test() {
        assertEquals("ac7fc7261c573830d19bf25ef20bf0d74d1443cd", engine.eval("sha1(\"SampleString\")"));
    }

    @Test
    void sha256Test() {
        assertEquals("77b12c9c6213a05fb60cd8151c51ca522e7ebd8e55096a6a8b2c34769ec4fc20", engine.eval("sha256(\"SampleString\")"));
    }

    @Test
    void sha512Test() {
        assertEquals("aee8e20df4b3ce730e2e4f04ca8a1becb522d769559c0791a04a4d18745caa8c6eb43c48ce265f1ceca33c65e789faace6e75bfa40dab0ece7e03c6fcda75961", engine.eval("sha512(\"SampleString\")"));
    }

    @Test
    void decode64Test() {
        assertEquals("Text", engine.eval("decode64(\"VGV4dA==\")"));
    }

    @Test
    void invalidDecode64Test() {
        try {
            engine.eval("decode64(\"!InvalidString!\")");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid base64 string in decode64: !InvalidString!", e.getMessage());
        }
    }

    @Test
    void encode64Test() {
        assertEquals("U2FtcGxlU3RyaW5n", engine.eval("encode64(\"SampleString\")"));
        assertEquals("", engine.eval("encode64(\"\")"));
    }
}
