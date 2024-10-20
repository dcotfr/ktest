package ktest.domain.xlsx;

import ktest.core.XmlUtils;

import java.util.ArrayList;
import java.util.List;

final class Workbook implements XmlUtils {
    private final SharedStrings sharedStrings = new SharedStrings();
    private final Styles styles = new Styles();
    private final List<Worksheet> worksheets = new ArrayList<>();

    Worksheet createWorksheet(final String pName) {
        final var res = new Worksheet(pName, this);
        worksheets.add(res);
        return res;
    }

    SharedStrings sharedStrings() {
        return sharedStrings;
    }

    Styles styles() {
        return styles;
    }

    @Override
    public String toXml() {
        final var res = new StringBuilder(XML_HEADER)
                .append("<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">")
                .append("<bookViews><workbookView activeTab=\"0\"/></bookViews>");

        res.append("<sheets>");
        worksheets.forEach(ws -> res.append("<sheet name=\"").append(fullClean(ws.name()))
                .append("\" sheetId=\"").append(worksheetId(ws))
                .append("\" r:id=\"rId").append(worksheetRId(ws)).append("\"/>"));
        res.append("</sheets>");

        return res.append("</workbook>").toString();
    }

    int worksheetId(final Worksheet pWorksheet) {
        return worksheets.indexOf(pWorksheet) + 1;
    }

    int worksheetRId(final Worksheet pWorksheet) {
        return worksheetId(pWorksheet) + 2;
    }

    List<Worksheet> worksheets() {
        return worksheets;
    }
}
