package ktest.script;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@QuarkusTest
class EngineOperatorsTest {
    @Inject
    private Engine engine;

    @Test
    void addTest() {
        assertEquals(8.6, engine.eval("5+3.6"));
        assertEquals(6L, engine.eval("1+2+3"));
        assertEquals(14L, engine.eval("2+(3+4)+5"));
        assertEquals(28L, engine.eval("((1+2)+(3+(4+5)+6))+7"));
        assertEquals(12.5, engine.eval("+12.5"));
    }

    @Test
    void invalidAddTest() {
        try {
            engine.eval("\"left\"+2");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in >>>left<<<+2", e.getMessage());
        }

        try {
            engine.eval("2.1+\"right\"");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 2.1+>>>right<<<", e.getMessage());
        }

        try {
            engine.eval("2++");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 2++", e.getMessage());
        }

        try {
            engine.eval("3.1+");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 3.1+", e.getMessage());
        }
    }

    @Test
    void subTest() {
        assertEquals(-3.5, engine.eval("2.5-6"));
        assertEquals(0L, engine.eval("3-2-1"));
        assertEquals(4L, engine.eval("(5-2)-(3-4)"));
        assertEquals(-12.5, engine.eval("-12.5"));
    }

    @Test
    void invalidSubTest() {
        try {
            engine.eval("\"left\"-(7-2)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in >>>left<<<-5", e.getMessage());
        }

        try {
            engine.eval("5-\"right\"");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 5->>>right<<<", e.getMessage());
        }

        try {
            engine.eval("5--");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 5--", e.getMessage());
        }

        try {
            engine.eval("5.3-");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 5.3-", e.getMessage());
        }
    }

    @Test
    void mulTest() {
        assertEquals(6.28, engine.eval("2 * 3.14"));
        assertEquals(24L, engine.eval("(2 * 3) * 4"));
        assertEquals(30.0, engine.eval("2.5 * (3 * 4)"));
    }

    @Test
    void invalidMulTest() {
        try {
            engine.eval("\"left\"*2");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in >>>left<<<*2", e.getMessage());
        }

        try {
            engine.eval("4*\"right\"");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 4*>>>right<<<", e.getMessage());
        }

        try {
            engine.eval("3.5*+");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 3.5*+", e.getMessage());
        }

        try {
            engine.eval("8.74*");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 8.74*", e.getMessage());
        }
    }

    @Test
    void divTest() {
        assertEquals(2.5, engine.eval("5/2"));
        assertEquals(2.0, engine.eval("12/2/3"));
        assertEquals(4.0, engine.eval("(80/10)/2"));
    }

    @Test
    void invalidDivTest() {
        try {
            engine.eval("5/0");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: divide by zero in 5/>>>0<<<", e.getMessage());
        }

        try {
            engine.eval("\"left\"/2.2");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in >>>left<<</2.2", e.getMessage());
        }

        try {
            engine.eval("4/\"right\"");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 4/>>>right<<<", e.getMessage());
        }

        try {
            engine.eval("3.12/-");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 3.12/-", e.getMessage());
        }

        try {
            engine.eval("8/");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Syntax error: a number was expected in 8/", e.getMessage());
        }
    }

    @Test
    void addMulTest() {
        assertEquals(7L, engine.eval("1 + 2 * 3"));
        assertEquals(9L, engine.eval("(1 + 2) * 3"));
        assertEquals(7.4, engine.eval("1.4 + (2 * 3)"));
    }
}
