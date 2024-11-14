package ktest.domain.xlsx;

import ktest.core.XmlUtils;

final class Cell implements XmlUtils {
    private final int column;
    private final int row;
    private final int style;
    private Object value;

    Cell(final int pColumn, final int pRow, final int pStyle) {
        column = pColumn;
        row = pRow;
        style = pStyle;
    }

    int column() {
        return column;
    }

    static String columnRef(int pColumn) {
        final var res = new StringBuilder();
        while (pColumn >= 0) {
            res.append((char) ('A' + (pColumn % 26)));
            pColumn = (pColumn / 26) - 1;
        }
        res.reverse();
        return res.toString();
    }

    String ref() {
        return ref(column, row);
    }

    static String ref(int pColumn, final int pRow) {
        return columnRef(pColumn) + rowRef(pRow);
    }

    int row() {
        return row;
    }

    static int rowRef(final int pRow) {
        return pRow + 1;
    }

    @Override
    public String toXml() {
        final var res = new StringBuilder("<c r=\"").append(ref()).append("\"");
        if (style != 0) {
            res.append(" s=\"").append(style).append('\"');
        }

        if (value == null) {
            return res.append("/>").toString();
        }

        final var formula = value instanceof Formula;
        if (!formula) {
            res.append(" t=\"").append(valueType()).append('\"');
        }
        res.append(">");

        if (formula) {
            res.append("<f>").append(((Formula) value).expression()).append("</f>");
        } else {
            res.append("<v>");
            if (value instanceof SharedString s) {
                res.append(s.index());
            } else if (value instanceof Number n) {
                res.append(n);
            }
            res.append("</v>");
        }
        return res.append("</c>").toString();
    }

    Cell value(final Object pObject) {
        value = pObject;
        return this;
    }

    private String valueType() {
        if (value instanceof SharedString) {
            return "s";
        }
        return "n";
    }
}
