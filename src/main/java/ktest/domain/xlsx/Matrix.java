package ktest.domain.xlsx;

import ktest.domain.Action;
import ktest.kafka.TopicRef;
import org.dhatim.fastexcel.ConditionalFormattingExpressionRule;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static ktest.domain.Action.*;
import static org.dhatim.fastexcel.BorderSide.*;
import static org.dhatim.fastexcel.BorderStyle.*;

public final class Matrix {
    private static final List<StepState> STEP_STATES = new ArrayList<>();

    private Matrix() {
    }

    public synchronized static StepState add(final String pTestCase, final TopicRef pTopicRef, final Action pAction, final List<String> pTags, final long pThread) {
        final var res = new StepState(pTestCase + '#' + pThread, pTopicRef.broker(), pTopicRef.topic(), pAction, pTags);
        STEP_STATES.add(res);
        return res;
    }

    public static void save(final String pFileName, final String pEnv) throws IOException {
        try (final var os = new FileOutputStream(pFileName);
             final var wb = new Workbook(os, "ktest", null)) {
            wb.properties().setTitle("Test Case Matrix");
            wb.setGlobalDefaultFont("Arial", 10.0);
            fillWorksheet(wb.newWorksheet("Content"), pEnv, true);
            fillWorksheet(wb.newWorksheet("Results"), pEnv, false);
        }
    }

    private synchronized static void fillWorksheet(final Worksheet pWorksheet, final String pEnv, final boolean pAll) {
        pWorksheet.value(0, 0, "Env: " + pEnv);
        pWorksheet.style(0, 0).bold().fontSize(12).verticalAlignment("center").set();
        pWorksheet.range(0, 0, 3, 0).merge();
        int c = 4;
        final var tcs = testCases();
        final var lastColumn = 3 + 4 * tcs.size();
        final var nbMaxTags = maxTags();
        pWorksheet.range(0, c, 0, lastColumn)
                .style().rotation(255).verticalAlignment("top").set();
        pWorksheet.range(1, c, 5 + nbMaxTags + tcs.size(), lastColumn)
                .style().verticalAlignment("center").horizontalAlignment("center").set();
        for (final var tc : tcs) {
            pWorksheet.value(0, c, tc);
            pWorksheet.range(0, c, 0, c + 3)
                    .style().borderStyle(LEFT, THIN).borderStyle(RIGHT, THIN)
                    .borderStyle(TOP, THIN)
                    .merge().set();
            final var tags = tags(tc);
            for (var i = 0; i < nbMaxTags; i++) {
                pWorksheet.value(1 + i, c, i < tags.size() ? tags.get(i) : null);
                pWorksheet.range(1 + i, c, 1 + i, c + 3)
                        .style().borderStyle(LEFT, THIN).borderStyle(RIGHT, THIN)
                        .fontSize(8).horizontalAlignment("center")
                        .merge().set();
            }
            pWorksheet.formula(1 + nbMaxTags, c, "SUM(" + pWorksheet.range(3 + nbMaxTags, c, 3 + nbMaxTags, c + 3) + ')');
            pWorksheet.range(1 + nbMaxTags, c, 1 + nbMaxTags, c + 3)
                    .style().borderStyle(LEFT, THIN).borderStyle(RIGHT, THIN)
                    .borderStyle(TOP, DOTTED).borderStyle(BOTTOM, DOTTED)
                    .bold().horizontalAlignment("center")
                    .merge().set();
            pWorksheet.value(2 + nbMaxTags, c, "S");
            pWorksheet.style(2 + nbMaxTags, c).borderStyle(LEFT, THIN).set();
            pWorksheet.value(2 + nbMaxTags, c + 1, "P");
            pWorksheet.value(2 + nbMaxTags, c + 2, "A");
            pWorksheet.value(2 + nbMaxTags, c + 3, "T");
            pWorksheet.style(2 + nbMaxTags, c + 3).borderStyle(RIGHT, THIN).set();
            c += 4;
        }
        pWorksheet.formula(1 + nbMaxTags, 1, "SUM(" + pWorksheet.range(1 + nbMaxTags, 4, 1 + nbMaxTags, lastColumn) + ')');
        pWorksheet.style(1 + nbMaxTags, 1).borderStyle(DOTTED).bold().horizontalAlignment("center").set();
        pWorksheet.range(1 + nbMaxTags, 2, 1 + nbMaxTags, 3)
                .style().borderStyle(TOP, DOTTED).borderStyle(BOTTOM, DOTTED).set();
        pWorksheet.range(2 + nbMaxTags, 1, 3 + nbMaxTags, 1)
                .style().borderStyle(LEFT, DOTTED).borderStyle(RIGHT, DOTTED).set();
        int r = 4 + nbMaxTags;
        final int startRow = r;
        for (final var b : brokers()) {
            final var topics = topics(b);
            pWorksheet.value(r, 0, b);
            pWorksheet.range(r, 0, r + topics.size() - 1, 0)
                    .style().borderStyle(TOP, THIN).borderStyle(BOTTOM, THIN)
                    .verticalAlignment("center")
                    .merge().set();
            pWorksheet.formula(r, 1, "SUM(" + pWorksheet.range(r, 3, r + topics.size() - 1, 3) + ')');
            pWorksheet.range(r, 1, r + topics.size() - 1, 1)
                    .style().borderStyle(TOP, THIN).borderStyle(BOTTOM, THIN)
                    .borderStyle(LEFT, DOTTED).borderStyle(RIGHT, DOTTED)
                    .verticalAlignment("center").horizontalAlignment("center").bold()
                    .merge().set();
            for (final var t : topics) {
                final var top = t.equals(topics.getFirst()) ? THIN : NONE;
                final var bottom = t.equals(topics.getLast()) ? THIN : NONE;
                pWorksheet.value(r, 2, t);
                pWorksheet.style(r, 2).borderStyle(TOP, top).borderStyle(BOTTOM, bottom).set();
                pWorksheet.formula(r, 3, "SUM(" + pWorksheet.range(r, 4, r, lastColumn) + ')');
                pWorksheet.style(r, 3).borderStyle(TOP, top).borderStyle(BOTTOM, bottom)
                        .horizontalAlignment("center").bold().set();
                conditionalFormating(pWorksheet, r, 3);
                c = 4;
                for (final var tc : tcs) {
                    int count = count(tc, b, t, SEND, pAll);
                    pWorksheet.style(r, c).borderStyle(LEFT, THIN)
                            .borderStyle(TOP, top).borderStyle(BOTTOM, bottom).set();
                    conditionalFormating(pWorksheet, r, c);
                    if (count != 0) {
                        pWorksheet.value(r, c, count);
                    }
                    count = count(tc, b, t, PRESENT, pAll);
                    pWorksheet.style(r, c + 1).borderStyle(TOP, top).borderStyle(BOTTOM, bottom).set();
                    conditionalFormating(pWorksheet, r, c + 1);
                    if (count != 0) {
                        pWorksheet.value(r, c + 1, count);
                    }
                    count = count(tc, b, t, ABSENT, pAll);
                    pWorksheet.style(r, c + 2).borderStyle(TOP, top).borderStyle(BOTTOM, bottom).set();
                    conditionalFormating(pWorksheet, r, c + 2);
                    if (count != 0) {
                        pWorksheet.value(r, c + 2, count);
                    }
                    count = count(tc, b, t, TODO, pAll);
                    pWorksheet.style(r, c + 3).borderStyle(RIGHT, THIN)
                            .borderStyle(TOP, top).borderStyle(BOTTOM, bottom).set();
                    conditionalFormating(pWorksheet, r, c + 3);
                    if (count != 0) {
                        pWorksheet.value(r, c + 3, count);
                    }
                    c += 4;
                }
                r++;
            }
        }
        c = 4;
        r--;
        for (final var _ : tcs) {
            pWorksheet.formula(startRow - 1, c, "SUM(" + pWorksheet.range(startRow, c, r, c) + ')');
            pWorksheet.style(startRow - 1, c).bold().borderStyle(LEFT, THIN).set();
            conditionalFormating(pWorksheet, startRow - 1, c);
            pWorksheet.formula(startRow - 1, c + 1, "SUM(" + pWorksheet.range(startRow, c + 1, r, c + 1) + ')');
            pWorksheet.style(startRow - 1, c + 1).bold().set();
            conditionalFormating(pWorksheet, startRow - 1, c + 1);
            pWorksheet.formula(startRow - 1, c + 2, "SUM(" + pWorksheet.range(startRow, c + 2, r, c + 2) + ')');
            pWorksheet.style(startRow - 1, c + 2).bold().set();
            conditionalFormating(pWorksheet, startRow - 1, c + 2);
            pWorksheet.formula(startRow - 1, c + 3, "SUM(" + pWorksheet.range(startRow, c + 3, r, c + 3) + ')');
            pWorksheet.style(startRow - 1, c + 3).bold().borderStyle(RIGHT, THIN).set();
            conditionalFormating(pWorksheet, startRow - 1, c + 3);
            c += 4;
        }
    }

    private static void conditionalFormating(final Worksheet pWorksheet, final int pRow, final int pColumn) {
        final var cellRef = "!$" + columnRef(pColumn) + '$' + rowRef(pRow);
        pWorksheet.style(pRow, pColumn).fillColor("FFFFAA95").set(new ConditionalFormattingExpressionRule("Content" + cellRef + "&lt;&gt;Results" + cellRef, true));
    }

    private synchronized static List<String> brokers() {
        return STEP_STATES
                .stream().map(StepState::broker).collect(Collectors.toCollection(TreeSet::new))
                .stream().toList();
    }

    private synchronized static int count(final String pTestCase, final String pBroker, final String pTopic, final Action pAction, final boolean pAll) {
        return (int) STEP_STATES
                .stream()
                .filter(s -> s.testCase().equals(pTestCase) && s.broker().equals(pBroker)
                        && s.topic().equals(pTopic) && s.action().equals(pAction)
                        && (pAll || s.succeeded()))
                .count();
    }

    private synchronized static List<String> tags(final String pTestCase) {
        for (final var s : STEP_STATES) {
            if (s.testCase().equals(pTestCase)) {
                return s.tags() != null ? s.tags().stream().sorted().toList() : Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private synchronized static int maxTags() {
        var res = 0;
        for (var s : STEP_STATES) {
            if (s.tags() != null && s.tags().size() > res) {
                res = s.tags().size();
            }
        }
        return res;
    }

    private synchronized static List<String> testCases() {
        return STEP_STATES
                .stream().map(StepState::testCase).collect(Collectors.toCollection(TreeSet::new))
                .stream().toList();
    }

    private synchronized static List<String> topics(final String pBroker) {
        return STEP_STATES
                .stream().filter(s -> pBroker.equals(s.broker())).map(StepState::topic).collect(Collectors.toCollection(TreeSet::new))
                .stream().toList();
    }

    private static String columnRef(int pColumn) {
        final var res = new StringBuilder();
        while (pColumn >= 0) {
            res.append((char) ('A' + (pColumn % 26)));
            pColumn = (pColumn / 26) - 1;
        }
        res.reverse();
        return res.toString();
    }

    private static int rowRef(final int pRow) {
        return pRow + 1;
    }
}
