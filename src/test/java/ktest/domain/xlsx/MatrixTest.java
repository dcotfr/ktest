package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatrixTest {
    @Test
    void columnRefTest() {
        assertEquals("A", Matrix.columnRef(0));
        assertEquals("Z", Matrix.columnRef(25));
        assertEquals("AA", Matrix.columnRef(26));
        assertEquals("AW", Matrix.columnRef(48));
        assertEquals("BZ", Matrix.columnRef(77));
        assertEquals("CA", Matrix.columnRef(78));
        assertEquals("GX", Matrix.columnRef(205));
    }
}
