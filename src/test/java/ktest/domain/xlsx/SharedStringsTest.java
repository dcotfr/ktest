package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SharedStringsTest {
    @Test
    void test() {
        final var sharedStrings = new SharedStrings();
        assertEquals(new SharedString("A", 0), sharedStrings.cache("A"));
        assertEquals(new SharedString("Z", 1), sharedStrings.cache("Z"));
        assertEquals(new SharedString("A", 0), sharedStrings.cache("A"));
        assertEquals(new SharedString("B", 2), sharedStrings.cache("B"));
        assertEquals(new SharedString("C", 3), sharedStrings.cache("C"));
        assertEquals(new SharedString("A", 0), sharedStrings.cache("A"));

        final var expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" count=\"6\" uniqueCount=\"4\">"
                + "<si><t>A</t></si><si><t>Z</t></si><si><t>B</t></si><si><t>C</t></si>"
                + "</sst>";
        assertEquals(expected, sharedStrings.toXml());
    }
}
