package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlignmentTest {
    @Test
    void testToXml() {
        assertEquals("<alignment vertical=\"top\"/>", new Alignment(false, false, false).toXml());
        assertEquals("<alignment vertical=\"center\" textRotation=\"255\"/>", new Alignment(true, false, true).toXml());
        assertEquals("<alignment vertical=\"center\" horizontal=\"center\" textRotation=\"255\"/>", new Alignment(true, true, true).toXml());
    }
}
