package ktest.script.func.crypt;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.script.Engine;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.smallrye.common.constraint.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class CryptFunctionsTest {
    @Inject
    Engine engine;

    @Test
    void aesencdecTest() {
        final var key = "MSJsb1E+TXozA6D0wAsuWUSEYFq2lNHXPEyJKuLrCzE=";
        final var encrypted = engine.eval("aesenc(\"ToBeEncryptedString\", \"" + key + "\")");
        assertEquals("ToBeEncryptedString", engine.eval("aesdec(\"" + encrypted + "\", \"" + key + "\")"));
    }

    @Test
    void aeskeyTest() {
        assertNotNull(Base64.getDecoder().decode(engine.eval("aeskey()").toString()));
    }
}
