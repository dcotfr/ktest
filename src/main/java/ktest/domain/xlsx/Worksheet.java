package ktest.domain.xlsx;

import ktest.core.XmlUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ktest.domain.xlsx.Alignment.CENTER;
import static ktest.domain.xlsx.Border.NONE;

final class Worksheet implements XmlUtils {
    private final String name;
    private final Workbook workbook;
    private final List<Cell> cells = new ArrayList<>();
    private final List<Range> mergeCells = new ArrayList<>();

    Worksheet(final String pName, final Workbook pWorkbook) {
        name = pName;
        workbook = pWorkbook;
    }

    Worksheet cell(final int pColumn, final int pRow, final Object pValue) {
        return cell(pColumn, pRow, pValue, Font.PLAIN, NONE, CENTER);
    }

    Worksheet cell(final int pColumn, final int pRow, final Object pValue, final Font pFont, final Border pBorder, final Alignment pAlignment) {
        final var value = pValue instanceof String s ? workbook.sharedStrings().cache(s) : pValue;
        cells.add(new Cell(pColumn, pRow, workbook.styles().xfId(pFont, pBorder, pAlignment)).value(value));
        return this;
    }

    private static int firstColumn(final List<Cell> pRow) {
        return pRow.stream().mapToInt(Cell::column).min().orElse(0);
    }

    private int firstRow() {
        return cells.stream().mapToInt(Cell::row).min().orElse(0);
    }

    private static int lastColumn(final List<Cell> pRow) {
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

        res.append("<sheetData>");
        final var lastRow = lastRow();
        for (var i = firstRow(); i <= lastRow; i++) {
            final List<Cell> row = cellsOfRow(i);
            final var firstColumn = firstColumn(row) + 1;
            final var lastColumn = lastColumn(row) + 1;
            res.append("<row r=\"").append(i + 1).append("\" spans=\"").append(firstColumn).append(':').append(lastColumn).append("\">");
            row.stream()
                    .sorted(Comparator.comparingInt(Cell::column))
                    .forEach(c -> res.append(c.toXml()));
            res.append("</row>");
        }
        res.append("</sheetData>");

        if (!mergeCells.isEmpty()) {
            res.append("<mergeCells count=\"").append(mergeCells.size()).append("\">");
            mergeCells.forEach(r -> res.append("<mergeCell ref=\"").append(r.toString()).append("\"/>"));
            res.append("</mergeCells>");
        }

        return res.append("</worksheet>").toString();
    }
}
