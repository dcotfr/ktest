package ktest.domain.xlsx;

import ktest.core.XmlUtils;

final class Rels implements XmlUtils {
    private final Workbook workbook;

    Rels(final Workbook pWorkbook) {
        workbook = pWorkbook;
    }

    @Override
    public String toXml() {
        final var res = new StringBuilder(XML_HEADER)
                .append("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">");
        if (workbook == null) {
            res.append("<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>");
        } else {
            res.append("<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings\" Target=\"sharedStrings.xml\"/>")
                    .append("<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>");
            workbook.worksheets()
                    .forEach(ws -> res.append("<Relationship Id=\"rId").append(workbook.worksheetRId(ws))
                            .append("\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet")
                            .append(workbook.worksheetId(ws))
                            .append(".xml\"/>"));
        }
        return res.append("</Relationships>").toString();
    }
}
