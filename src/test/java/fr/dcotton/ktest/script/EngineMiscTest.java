package fr.dcotton.ktest.script;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EngineMiscTest {
    @Inject
    private Engine engine;

    @Test
    void letTest() {
        engine.reset().eval("a=(-10)");
        assertEquals(-10L, engine.context().variable("a").value());
        engine.reset().eval("a=-10.5");
        assertEquals(-10.5, engine.context().variable("a").value());
        engine.reset().eval("a=+2");
        assertEquals(2L, engine.context().variable("a").value());

        engine.reset().eval("x=(3*5.5)");
        assertEquals(16.5, engine.context().variable("x").value());
        assertNull(engine.context().variable("str"));
        engine.eval("str=\"essai\"");
        assertEquals(16.5, engine.context().variable("x").value());
        assertEquals("essai", engine.context().variable("str").value());
    }

    @Test
    void invalidLetTest() {
        try {
            engine.reset().eval("var=");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: missing value to affect in >>>var<<<", e.getMessage());
        }
    }

    @Test
    void varTest() {
        engine.reset().context().variable("x", 1.5).variable("str", "Essai").variable("y", 2L);
        assertEquals(1.5, engine.eval("x"));
        assertEquals(2L, engine.eval("y"));
        assertEquals(4.5, engine.eval("y*x+x"));
        assertEquals("Essai", engine.eval("str"));
    }

    @Test
    void invalidVarTest() {
        try {
            engine.reset().eval("a=b");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: unknown variable in a>>>b<<<", e.getMessage());
        }
    }

    @Test
    void varLetTest() {
        engine.reset().eval("x = 10");
        assertEquals(10L, engine.context().variable("x").value());
        engine.eval("y = x + 20");
        assertEquals(10L, engine.context().variable("x").value());
        assertEquals(30L, engine.context().variable("y").value());
        engine.eval("z = y * x");
        assertEquals(10L, engine.context().variable("x").value());
        assertEquals(30L, engine.context().variable("y").value());
        assertEquals(300L, engine.context().variable("z").value());
    }
}
