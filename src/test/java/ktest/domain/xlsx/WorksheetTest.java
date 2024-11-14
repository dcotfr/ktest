package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorksheetTest {
    @Test
    void test() {
        final var worksheet = new Workbook().createWorksheet("Sheet1");
        final var expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<dimension ref=\"A1\"/>"
                + "<sheetData><row r=\"1\" spans=\"1:1\"></row></sheetData>"
                + "</worksheet>";
        assertEquals(expected, worksheet.toXml());
    }
}
