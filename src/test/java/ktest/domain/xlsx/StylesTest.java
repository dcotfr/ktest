package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StylesTest {
    @Test
    void testToXml() {
        final var expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<fonts count=\"1\"><font><name val=\"Arial\"/><color rgb=\"FF000000\"/><sz val=\"10\"/></font></fonts>"
                + "<borders count=\"1\"><border/></borders>"
                + "<cellXfs count=\"0\"></cellXfs>"
                + "</styleSheet>";
        assertEquals(expected, new Styles().toXml());
    }
}
