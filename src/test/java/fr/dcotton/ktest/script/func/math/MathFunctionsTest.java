package fr.dcotton.ktest.script.func.math;

import fr.dcotton.ktest.script.Engine;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void minTest() {
        assertEquals(2.0, engine.eval("min(5.0,2)"));
        assertEquals(1L, engine.eval("min(1,2)"));
        assertEquals(-3.5, engine.eval("min(-3.5,2.6)"));
    }

    @Test
    void roundTest() {
        assertEquals(3L, engine.eval("round(3.14)"));
        assertEquals(4L, engine.eval("round(3.54)"));
    }
}
