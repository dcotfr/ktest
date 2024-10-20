package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RangeTest {
    @Test
    void testToString() {
        assertEquals("A1", new Range(0, 0, 0, 0).toString());
        assertEquals("B2:C3", new Range(1, 1, 2, 2).toString());
        assertEquals("A1:D4", new Range(3, 3, 0, 0).toString());
    }
}
