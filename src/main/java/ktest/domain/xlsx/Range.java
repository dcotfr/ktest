package ktest.domain.xlsx;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static ktest.domain.xlsx.Cell.ref;

record Range(int columnStart, int rowStart, int columnEnd, int rowEnd) {
    boolean isMultiCell() {
        return columnStart != columnEnd || rowStart != rowEnd;
    }

    @Override
    public String toString() {
        final var res = new StringBuilder(ref(min(columnStart, columnEnd), min(rowStart, rowEnd)));
        if (isMultiCell()) {
            res.append(':').append(ref(max(columnStart, columnEnd), max(rowStart, rowEnd)));
        }
        return res.toString();
    }
}
