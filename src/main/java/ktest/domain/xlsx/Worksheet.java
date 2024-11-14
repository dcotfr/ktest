package ktest.domain.xlsx;

import ktest.core.XmlUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ktest.domain.xlsx.Alignment.CENTER;
import static ktest.domain.xlsx.Border.NONE;
import static ktest.domain.xlsx.Cell.columnRef;
import static ktest.domain.xlsx.Cell.rowRef;

final class Worksheet implements XmlUtils {
    private final String name;
    private final Workbook workbook;
    private final List<Cell> cells = new ArrayList<>();
    private final List<Range> mergeCells = new ArrayList<>();
    private final List<String> conditionals = new ArrayList<>();

    Worksheet(final String pName, final Workbook pWorkbook) {
        name = pName;
        workbook = pWorkbook;
    }

    Worksheet cell(final int pColumn, final int pRow, final Object pValue) {
        return cell(pColumn, pRow, pValue, Font.PLAIN, NONE, CENTER);
    }

    Worksheet cell(final int pColumn, final int pRow, final Object pValue, final Font pFont, final Border pBorder, final Alignment pAlignment) {
        return cell(pColumn, pRow, pValue, pFont, pBorder, pAlignment, null);
    }

    Worksheet cell(final int pColumn, final int pRow, final Object pValue, final Font pFont, final Border pBorder, final Alignment pAlignment, final String pConditional) {
        final var value = pValue instanceof String s ? workbook.sharedStrings().cache(s) : pValue;
        cells.add(new Cell(pColumn, pRow, workbook.styles().xfId(pFont, pBorder, pAlignment)).value(value));
        if (pConditional != null) {
            conditionals.add(pConditional + "!$" + columnRef(pColumn) + '$' + rowRef(pRow));
        }
        return this;
    }

    private int firstColumn() {
        return cells.stream().mapToInt(Cell::column).min().orElse(0);
    }

    private static int firstColumnOfRow(final List<Cell> pRow) {
        return pRow.stream().mapToInt(Cell::column).min().orElse(0);
    }

    private int firstRow() {
        return cells.stream().mapToInt(Cell::row).min().orElse(0);
    }

    private int lastColumn() {
        return cells.stream().mapToInt(Cell::column).max().orElse(0);
    }

    private static int lastColumnOfRow(final List<Cell> pRow) {
        return pRow.stream().mapToInt(Cell::column).max().orElse(0);
    }

    private int lastRow() {
        return cells.stream().mapToInt(Cell::row).max().orElse(0);
    }

    Worksheet merge(final Range pRange) {
        if (pRange.isMultiCell()) {
            mergeCells.add(pRange);
        }
        return this;
    }

    String name() {
        return name;
    }

    List<Cell> cellsOfRow(final int pRow) {
        return cells.stream()
                .filter(c -> c.row() == pRow)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toXml() {
        final var res = new StringBuilder(XML_HEADER)
                .append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");

        final var firstRow = firstRow();
        final var lastRow = lastRow();
        res.append("<dimension ref=\"" + new Range(firstColumn(), firstRow, lastColumn(), lastRow) + "\"/>");

        res.append("<sheetData>");
        for (var i = firstRow; i <= lastRow; i++) {
            final List<Cell> row = cellsOfRow(i);
            final var firstColumnOfRow = firstColumnOfRow(row) + 1;
            final var lastColumnOfRow = lastColumnOfRow(row) + 1;
            res.append("<row r=\"").append(i + 1).append("\" spans=\"").append(firstColumnOfRow).append(':').append(lastColumnOfRow).append("\">");
            row.stream()
                    .sorted(Comparator.comparingInt(Cell::column))
                    .forEach(c -> res.append(c.toXml()));
            res.append("</row>");
        }
        res.append("</sheetData>");

        conditionals.forEach(c -> {
            final var colRef = c.substring(c.indexOf("!") + 1).replace("$", "");
            res
                    .append("<conditionalFormatting sqref=\"").append(colRef).append("\">")
                    .append("<cfRule type=\"cellIs\" priority=\"2\" operator=\"notEqual\" dxfId=\"0\">")
                    .append("<formula>").append(c).append("</formula>")
                    .append("</cfRule>")
                    .append("</conditionalFormatting>");
        });

        if (!mergeCells.isEmpty()) {
            res.append("<mergeCells count=\"").append(mergeCells.size()).append("\">");
            mergeCells.forEach(r -> res.append("<mergeCell ref=\"").append(r.toString()).append("\"/>"));
            res.append("</mergeCells>");
        }

        return res.append("</worksheet>").toString();
    }
}
