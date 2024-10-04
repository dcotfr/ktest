package ktest.script.func.hex;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.script.Engine;
import ktest.script.ScriptException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class HexFunctionsTest {
    @Inject
    Engine engine;

    @Test
    void hex2IntTest() {
        assertEquals(32767L, engine.eval("hex2int(\"7fff\")"));
        try {
            engine.eval("hex2int(\"NotHexa\")");
            fail();
        } catch (final ScriptException e) {
            assertEquals("A valid hexadecimal string is expected in hex2int: NotHexa found.", e.getMessage());
        }
    }

    @Test
    void int2hexTest() {
        assertEquals("7fff", engine.eval("int2hex(32767)"));
        assertEquals("0", engine.eval("int2hex(0.0)"));
        assertEquals("fffffffffffffff4", engine.eval("int2hex(-12)"));
    }
}
