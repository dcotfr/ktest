package ktest.domain.xlsx;

import ktest.core.XmlUtils;

final class ContentTypes implements XmlUtils {
    private final Workbook workbook;

    ContentTypes(final Workbook pWorkbook) {
        workbook = pWorkbook;
    }

    @Override
    public String toXml() {
        final var res = new StringBuilder(XML_HEADER)
                .append("<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">")
                .append("<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>")
                .append("<Default Extension=\"xml\" ContentType=\"application/xml\"/>")
                .append("<Override PartName=\"/xl/sharedStrings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml\"/>")
                .append("<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>")
                .append("<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>");
        workbook.worksheets()
                .forEach(ws -> res.append("<Override PartName=\"/xl/worksheets/sheet").append(workbook.worksheetId(ws))
                        .append(".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"));
        return res.append("</Types>").toString();
    }
}
