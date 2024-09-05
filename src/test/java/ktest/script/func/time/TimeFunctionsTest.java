package ktest.script.func.time;

import ktest.script.Engine;
import ktest.script.ScriptException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class TimeFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void time2txtTest() {
        assertEquals("2024-02-25 10:53:41", engine.eval("time2txt(\"yyyy-MM-dd HH:mm:ss\", 1708854821321)"));
        assertEquals("2010-02-25 12:53:41", engine.eval("time2txt(\"2010-MM-dd 12:mm:ss\", 1708854821321)"));
    }

    @Test
    void invalidTime2TxtTest() {
        try {
            engine.eval("time2txt(\"aaaa-MM-jj\", 1708854821321)");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid date/time format in time2txt: aaaa-MM-jj", e.getMessage());
        }
    }

    @Test
    void txt2timeTest() {
        assertEquals(1721167200000L, engine.eval("txt2time(\"yyyy/MM/dd\", \"2024/07/17\")"));
    }

    @Test
    void invalidTxt2timeTest() {
        try {
            engine.eval("txt2time(\"aaaa/mm/jj\", \"2024/07/17\")");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid date/time format in txt2time: aaaa/mm/jj", e.getMessage());
        }

        try {
            engine.eval("txt2time(\"yyyy-MM-dd\", \"year/07/17\")");
            fail();
        } catch (final ScriptException e) {
            assertEquals("Invalid date string in txt2time: year/07/17", e.getMessage());
        }
    }

    @Test
    void nowTest() {
        final var before = System.currentTimeMillis();
        final var t = (Long) engine.eval("now()");
        final var after = System.currentTimeMillis();
        assertTrue(before <= t && t <= after);
    }
}
