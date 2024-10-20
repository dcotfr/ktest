package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlignmentTest {
    @Test
    void testToXml() {
        assertEquals("<alignment vertical=\"center\"/>", new Alignment(false, false).toXml());
        assertEquals("<alignment vertical=\"center\" textRotation=\"255\"/>", new Alignment(false, true).toXml());
        assertEquals("<alignment vertical=\"center\" horizontal=\"center\" textRotation=\"255\"/>", new Alignment(true, true).toXml());
    }
}
