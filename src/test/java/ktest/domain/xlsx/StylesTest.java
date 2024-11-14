package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StylesTest {
    @Test
    void testToXml() {
        final var expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\""
                + " xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\""
                + " xmlns:x14ac=\"http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac\""
                + " xmlns:x16r2=\"http://schemas.microsoft.com/office/spreadsheetml/2015/02/main\""
                + " xmlns:xr=\"http://schemas.microsoft.com/office/spreadsheetml/2014/revision\""
                + " mc:Ignorable=\"x14ac x16r2 xr\">"
                + "<fonts count=\"1\" x14ac:knownFonts=\"1\"><font><name val=\"Arial\"/><color rgb=\"FF000000\"/><sz val=\"10\"/></font></fonts>"
                + "<borders count=\"1\"><border diagonalUp=\"0\" diagonalDown=\"0\"/></borders>"
                + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                + "<cellXfs count=\"0\"></cellXfs>"
                + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                + "<dxfs count=\"1\"><dxf><fill><patternFill><bgColor rgb=\"FFFFAA95\"/></patternFill></fill></dxf></dxfs>"
                + "</styleSheet>";
        assertEquals(expected, new Styles().toXml());
    }
}
