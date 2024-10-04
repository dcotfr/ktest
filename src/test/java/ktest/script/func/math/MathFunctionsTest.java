package ktest.script.func.math;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.script.Engine;
import ktest.script.ScriptException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class MathFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void absTest() {
        assertEquals(3.14, engine.eval("abs(-3.14)"));
        assertEquals(5.4321, engine.eval("abs(5.4321)"));
        assertEquals(123L, engine.eval("abs(-123)"));
        assertEquals(321L, engine.eval("abs(321)"));
    }

    @Test
    void ceilTest() {
        assertEquals(4L, engine.eval("ceil(3.14)"));
    }

    @Test
    void floorTest() {
        assertEquals(3L, engine.eval("floor(3.14)"));
    }

    @Test
    void maxTest() {
        assertEquals(5.0, engine.eval("max(5.0,2)"));
        assertEquals(2L, engine.eval("max(1,2)"));
        assertEquals(2.6, engine.eval("max(-3.5,2.6)"));
    }

    @Test
    void maxUnboundedTest() {
        assertEquals(1L, engine.eval("max(1)"));
        assertEquals(3.3, engine.eval("max(1,-2,3.3,-4.4)"));
        try {
            engine.eval("max()");
            fail();
        } catch (final ScriptException e) {
            assertEquals("At least one number argument required.", e.getMessage());
        }
    }

    @Test
    void minTest() {
        assertEquals(2.0, engine.eval("min(5.0,2)"));
        assertEquals(1L, engine.eval("min(1,2)"));
        assertEquals(-3.5, engine.eval("min(-3.5,2.6)"));
    }

    @Test
    void minUnboundedTest() {
        assertEquals(1L, engine.eval("min(1)"));
        assertEquals(-4.4, engine.eval("min(1,-2,3.3,-4.4)"));
        try {
            engine.eval("min()");
            fail();
        } catch (final ScriptException e) {
            assertEquals("At least one number argument required.", e.getMessage());
        }
    }

    @Test
    void powTest() {
        assertEquals(256.0, engine.eval("pow(2,8)"));
    }

    @Test
    void roundTest() {
        assertEquals(3L, engine.eval("round(3.14)"));
        assertEquals(4L, engine.eval("round(3.54)"));
    }

    @Test
    void sgnTest() {
        assertEquals(-1L, engine.eval("sgn(-9.63)"));
        assertEquals(0L, engine.eval("sgn(0.0)"));
        assertEquals(1L, engine.eval("sgn(963)"));
    }

    @Test
    void sqrTest() {
        assertEquals(1.4142135623730951, engine.eval("sqr(2)"));
        try {
            engine.eval("sqr()");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid number of arguments in sqr: 1 expected, 0 found.", e.getMessage());
        }
        try {
            engine.eval("sqr(-5)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("A positive number is expected in sqr: -5.0 found.", e.getMessage());
        }
    }
}
