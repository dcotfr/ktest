package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FontTest {
    @Test
    void testToXml() {
        var expected = "<font><name val=\"Arial\"/><color rgb=\"FF000000\"/><sz val=\"10\"/></font>";
        assertEquals(expected, new Font("Arial", "FF000000", 10, false).toXml());

        expected = "<font><name val=\"Times\"/><color rgb=\"FFFF0000\"/><sz val=\"12\"/><b/></font>";
        assertEquals(expected, new Font("Times", "FFFF0000", 12, true).toXml());
    }
}
