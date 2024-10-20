package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkbookTest {
    @Test
    void testToXml() {
        final var expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<bookViews><workbookView activeTab=\"0\"/></bookViews>"
                + "<sheets>"
                + "<sheet name=\"First\" sheetId=\"1\" r:id=\"rId3\"/>"
                + "<sheet name=\"Second\" sheetId=\"2\" r:id=\"rId4\"/>"
                + "</sheets>"
                + "</workbook>";
        final var wb = new Workbook();
        wb.createWorksheet("First");
        wb.createWorksheet("Second");
        assertEquals(expected, wb.toXml());
    }
}
