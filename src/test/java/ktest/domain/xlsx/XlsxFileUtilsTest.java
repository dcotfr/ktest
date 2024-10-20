package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static ktest.domain.xlsx.Alignment.LEFT;
import static ktest.domain.xlsx.Alignment.ROTATED;
import static ktest.domain.xlsx.Border.Style.*;
import static ktest.domain.xlsx.Font.BOLD;

class XlsxFileUtilsTest {
    @Test
    void testSave() throws IOException {
        final var wb = new Workbook();
        final var ws = wb.createWorksheet("Summary");
        ws
                .cell(4, 0, "TestCase1", null, new Border(THIN, NONE, THIN, NONE), ROTATED)
                .cell(5, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                .cell(6, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                .cell(7, 0, null, null, new Border(NONE, THIN, THIN, NONE), null)
                .merge(new Range(4, 0, 7, 0))
                .cell(8, 0, "TestCase2", null, new Border(THIN, NONE, THIN, NONE), ROTATED)
                .cell(9, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                .cell(10, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                .cell(11, 0, null, null, new Border(NONE, THIN, THIN, NONE), null)
                .merge(new Range(8, 0, 11, 0))
                .cell(12, 0, "TestCase3", null, new Border(THIN, NONE, THIN, NONE), ROTATED)
                .cell(13, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                .cell(14, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                .cell(15, 0, null, null, new Border(NONE, THIN, THIN, NONE), null)
                .merge(new Range(12, 0, 15, 0))
                .cell(1, 1, Formula.sum(new Range(1, 4, 1, 6)), BOLD, new Border(DOTTED, DOTTED, DOTTED, DOTTED), null)
                .cell(2, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(3, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .merge(new Range(4, 1, 7, 1))
                .cell(4, 1, Formula.sum(new Range(4, 3, 7, 3)), BOLD, new Border(THIN, NONE, DOTTED, DOTTED), null)
                .cell(5, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(6, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(7, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .merge(new Range(8, 1, 11, 1))
                .cell(8, 1, Formula.sum(new Range(8, 3, 11, 3)), BOLD, new Border(THIN, NONE, DOTTED, DOTTED), null)
                .cell(9, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(10, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(11, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .merge(new Range(12, 1, 15, 1))
                .cell(12, 1, Formula.sum(new Range(12, 3, 15, 3)), BOLD, new Border(THIN, NONE, DOTTED, DOTTED), null)
                .cell(13, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(14, 1, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(15, 1, null, null, new Border(NONE, THIN, DOTTED, DOTTED), null)
                .cell(1, 2, null, null, new Border(DOTTED, DOTTED, NONE, NONE), null)
                .cell(4, 2, "S", null, new Border(THIN, NONE, NONE, NONE), null)
                .cell(5, 2, "P")
                .cell(6, 2, "A")
                .cell(7, 2, "T")
                .cell(8, 2, "S", null, new Border(THIN, NONE, NONE, NONE), null)
                .cell(9, 2, "P")
                .cell(10, 2, "A")
                .cell(11, 2, "T")
                .cell(12, 2, "S", null, new Border(THIN, NONE, NONE, NONE), null)
                .cell(13, 2, "P")
                .cell(14, 2, "A")
                .cell(15, 2, "T", null, new Border(NONE, THIN, NONE, NONE), null)
                .cell(1, 3, null, null, new Border(DOTTED, DOTTED, NONE, NONE), null)
                .cell(4, 3, Formula.sum(new Range(4, 4, 4, 6)), BOLD, new Border(THIN, NONE, NONE, THIN), null)
                .cell(5, 3, Formula.sum(new Range(5, 4, 5, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(6, 3, Formula.sum(new Range(6, 4, 6, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(7, 3, Formula.sum(new Range(7, 4, 7, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(8, 3, Formula.sum(new Range(8, 4, 8, 6)), BOLD, new Border(THIN, NONE, NONE, THIN), null)
                .cell(9, 3, Formula.sum(new Range(9, 4, 9, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(10, 3, Formula.sum(new Range(10, 4, 10, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(11, 3, Formula.sum(new Range(11, 4, 11, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(12, 3, Formula.sum(new Range(12, 4, 12, 6)), BOLD, new Border(THIN, NONE, NONE, THIN), null)
                .cell(13, 3, Formula.sum(new Range(13, 4, 13, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(14, 3, Formula.sum(new Range(14, 4, 14, 6)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                .cell(15, 3, Formula.sum(new Range(15, 4, 15, 6)), BOLD, new Border(NONE, THIN, NONE, THIN), null)
                .cell(0, 4, "Broker1", null, new Border(THIN, NONE, THIN, NONE), LEFT)
                .cell(1, 4, Formula.sum(new Range(3, 4, 3, 5)), BOLD, new Border(DOTTED, DOTTED, THIN, NONE), null)
                .cell(2, 4, "Topic1", null, new Border(NONE, NONE, THIN, NONE), LEFT)
                .cell(3, 4, Formula.sum(new Range(4, 4, 15, 4)), BOLD, new Border(NONE, THIN, THIN, NONE), null)
                .cell(4, 4, 1, null, new Border(THIN, NONE, THIN, NONE), null)
                .cell(8, 4, 1, null, new Border(THIN, NONE, THIN, NONE), null)
                .cell(12, 4, 5, null, new Border(THIN, NONE, THIN, NONE), null)
                .cell(15, 4, null, null, new Border(NONE, THIN, THIN, NONE), null)
                .cell(0, 5, null, null, new Border(THIN, DOTTED, NONE, THIN), null)
                .cell(1, 5, null, null, new Border(DOTTED, DOTTED, NONE, THIN), null)
                .cell(2, 5, "Topic2", null, new Border(NONE, NONE, NONE, THIN), LEFT)
                .cell(3, 5, Formula.sum(new Range(4, 5, 15, 5)), BOLD, new Border(NONE, THIN, NONE, THIN), null)
                .cell(4, 5, null, null, new Border(THIN, NONE, NONE, THIN), null)
                .cell(5, 5, 1, null, new Border(NONE, NONE, NONE, THIN), null)
                .cell(6, 5, null, null, new Border(NONE, NONE, NONE, THIN), null)
                .cell(7, 5, null, null, new Border(NONE, THIN, NONE, THIN), null)
                .cell(8, 5, 2, null, new Border(THIN, NONE, NONE, THIN), null)
                .cell(9, 5, 1, null, new Border(NONE, NONE, NONE, THIN), null)
                .cell(10, 5, null, null, new Border(NONE, NONE, NONE, THIN), null)
                .cell(11, 5, 1, null, new Border(NONE, THIN, NONE, THIN), null)
                .cell(12, 5, null, null, new Border(THIN, NONE, NONE, THIN), null)
                .cell(13, 5, null, null, new Border(NONE, NONE, NONE, THIN), null)
                .cell(14, 5, null, null, new Border(NONE, NONE, NONE, THIN), null)
                .cell(15, 5, null, null, new Border(NONE, THIN, NONE, THIN), null)
                .merge(new Range(0, 4, 0, 5))
                .merge(new Range(1, 4, 1, 5))
                .cell(0, 6, "Broker2", null, new Border(THIN, DOTTED, THIN, THIN), LEFT)
                .cell(1, 6, Formula.sum(new Range(3, 6, 3, 6)), BOLD, new Border(DOTTED, DOTTED, THIN, THIN), null)
                .cell(2, 6, "TopicA", null, new Border(DOTTED, NONE, THIN, THIN), LEFT)
                .cell(3, 6, Formula.sum(new Range(4, 6, 15, 6)), BOLD, new Border(NONE, THIN, THIN, THIN), null)
                .cell(4, 6, null, null, new Border(THIN, NONE, THIN, THIN), null)
                .cell(5, 6, null, null, new Border(NONE, NONE, THIN, THIN), null)
                .cell(6, 6, null, null, new Border(NONE, NONE, THIN, THIN), null)
                .cell(7, 6, null, null, new Border(NONE, THIN, THIN, THIN), null)
                .cell(8, 6, null, null, new Border(THIN, NONE, THIN, THIN), null)
                .cell(9, 6, 2, null, new Border(NONE, NONE, THIN, THIN), null)
                .cell(10, 6, null, null, new Border(NONE, NONE, THIN, THIN), null)
                .cell(11, 6, null, null, new Border(NONE, THIN, THIN, THIN), null)
                .cell(12, 6, null, null, new Border(THIN, NONE, THIN, THIN), null)
                .cell(13, 6, 3, null, new Border(NONE, NONE, THIN, THIN), null)
                .cell(14, 6, 1, null, new Border(NONE, NONE, THIN, THIN), null)
                .cell(15, 6, 1, null, new Border(NONE, THIN, THIN, THIN), null)
                .merge(new Range(0, 6, 0, 6))
                .merge(new Range(1, 6, 1, 6));
        XlsxFileUtils.save(new File("report.xlsx"), wb);
    }
}
