package ktest.script.func.text;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import ktest.script.Engine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class TextFunctionsTest {
    @Inject
    private Engine engine;

    @Test
    void concatTestBinary() {
        assertEquals("AaaBbb", engine.eval("concat(\"Aaa\", \"Bbb\")"));
    }

    @Test
    void concatTestUnbound() {
        assertEquals("AbCd", engine.eval("concat(\"A\", \"b\", \"C\", \"d\")"));
    }

    @Test
    void concatTestEmpty() {
        assertEquals("", engine.eval("concat()"));
    }

    @Test
    void leftTest() {
        assertEquals("Sample", engine.eval("left(\"Sample\", 15)"));
        assertEquals("Samp", engine.eval("left(\"Sample\", 4)"));
        assertEquals("S", engine.eval("left(\"Sample\", 1)"));
        assertEquals("", engine.eval("left(\"Sample\", 0)"));
    }

    @Test
    void lowerTest() {
        assertEquals("tolower", engine.eval("lower(\"ToLower\")"));
    }

    @Test
    void ltrimTest() {
        assertEquals("Test ", engine.eval("ltrim(\" Test \")"));
    }

    @Test
    void rightTest() {
        assertEquals("Sample", engine.eval("right(\"Sample\", 15)"));
        assertEquals("mple", engine.eval("right(\"Sample\", 4)"));
        assertEquals("e", engine.eval("right(\"Sample\", 1)"));
        assertEquals("", engine.eval("right(\"Sample\", 0)"));
    }

    @Test
    void rtrimTest() {
        assertEquals(" Test", engine.eval("rtrim(\" Test \")"));
    }

    @Test
    void upperTest() {
        assertEquals("TOUPPER", engine.eval("upper(\"ToUpper\")"));
    }
}