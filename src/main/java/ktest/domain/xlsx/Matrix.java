package ktest.domain.xlsx;

import ktest.domain.Action;
import ktest.kafka.TopicRef;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static ktest.domain.Action.*;
import static ktest.domain.xlsx.Alignment.LEFT;
import static ktest.domain.xlsx.Alignment.ROTATED;
import static ktest.domain.xlsx.Border.Style.*;
import static ktest.domain.xlsx.Font.*;

public final class Matrix {
    private static final List<StepState> STEP_STATES = new ArrayList<>();

    private Matrix() {
    }

    public synchronized static StepState add(final String pTestCase, final TopicRef pTopicRef, final Action pAction, final List<String> pTags, final long pThread) {
        final var res = new StepState(pTestCase + '#' + pThread, pTopicRef.broker(), pTopicRef.topic(), pAction, pTags);
        STEP_STATES.add(res);
        return res;
    }

    public synchronized static void save(final String pFileName, final String pEnv) throws IOException {
        final var wb = new Workbook();
        fillWorksheet(wb.createWorksheet("Content"), pEnv, true);
        fillWorksheet(wb.createWorksheet("Results"), pEnv, false);
        XlsxFileUtils.save(new File(pFileName), wb);
    }

    private synchronized static void fillWorksheet(final Worksheet pWorksheet, final String pEnv, final boolean pAll) {
        pWorksheet
                .cell(0, 0, "Env: " + pEnv, BIG, null, LEFT)
                .merge(new Range(0, 0, 3, 0));
        int c = 4;
        final var tcs = testCases();
        final var lastColumn = 3 + 4 * tcs.size();
        final var nbMaxTags = maxTags();
        for (final var tc : tcs) {
            pWorksheet
                    .cell(c, 0, tc, null, new Border(THIN, NONE, THIN, NONE), ROTATED)
                    .cell(c + 1, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                    .cell(c + 2, 0, null, null, new Border(NONE, NONE, THIN, NONE), null)
                    .cell(c + 3, 0, null, null, new Border(NONE, THIN, THIN, NONE), null)
                    .merge(new Range(c, 0, c + 3, 0));
            final var tags = tags(tc);
            for (var i = 0; i < nbMaxTags; i++) {
                pWorksheet
                        .cell(c, 1 + i, i < tags.size() ? tags.get(i) : null, SMALL, new Border(THIN, NONE, NONE, NONE), null)
                        .cell(c + 1, 1 + i, null, null, null, null)
                        .cell(c + 2, 1 + i, null, null, null, null)
                        .cell(c + 3, 1 + i, null, null, new Border(NONE, THIN, NONE, NONE), null)
                        .merge(new Range(c, 1 + i, c + 3, 1 + i));
            }
            pWorksheet
                    .cell(c, 1 + nbMaxTags, Formula.sum(new Range(c, 3 + nbMaxTags, c + 3, 3 + nbMaxTags)), BOLD, new Border(THIN, NONE, DOTTED, DOTTED), null)
                    .cell(c + 1, 1 + nbMaxTags, null, BOLD, new Border(NONE, NONE, DOTTED, DOTTED), null)
                    .cell(c + 2, 1 + nbMaxTags, null, BOLD, new Border(NONE, NONE, DOTTED, DOTTED), null)
                    .cell(c + 3, 1 + nbMaxTags, null, BOLD, new Border(NONE, THIN, DOTTED, DOTTED), null)
                    .merge(new Range(c, 1 + nbMaxTags, c + 3, 1 + nbMaxTags));
            pWorksheet
                    .cell(c, 2 + nbMaxTags, "S", null, new Border(THIN, NONE, DOTTED, NONE), null)
                    .cell(c + 1, 2 + nbMaxTags, "P", null, new Border(NONE, NONE, DOTTED, NONE), null)
                    .cell(c + 2, 2 + nbMaxTags, "A", null, new Border(NONE, NONE, DOTTED, NONE), null)
                    .cell(c + 3, 2 + nbMaxTags, "T", null, new Border(NONE, THIN, DOTTED, NONE), null);
            c += 4;
        }
        pWorksheet
                .cell(1, 1 + nbMaxTags, Formula.sum(new Range(4, 1 + nbMaxTags, lastColumn, 1 + nbMaxTags)), BOLD, new Border(DOTTED, DOTTED, DOTTED, DOTTED), null)
                .cell(2, 1 + nbMaxTags, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(3, 1 + nbMaxTags, null, null, new Border(NONE, NONE, DOTTED, DOTTED), null)
                .cell(1, 2 + nbMaxTags, null, null, new Border(DOTTED, DOTTED, DOTTED, NONE), null)
                .cell(1, 3 + nbMaxTags, null, null, new Border(DOTTED, DOTTED, NONE, THIN), null);
        int r = 4 + nbMaxTags;
        final int startRow = r;
        for (final var b : brokers()) {
            final var topics = topics(b);
            pWorksheet
                    .merge(new Range(0, r, 0, r + topics.size() - 1))
                    .merge(new Range(1, r, 1, r + topics.size() - 1));
            for (final var t : topics) {
                final var top = t.equals(topics.getFirst()) ? THIN : NONE;
                final var bottom = t.equals(topics.getLast()) ? THIN : NONE;
                pWorksheet
                        .cell(0, r, top == THIN ? b : null, null, new Border(THIN, DOTTED, top, bottom), LEFT)
                        .cell(1, r, top == THIN ? Formula.sum(new Range(3, r, 3, r + topics.size() - 1)) : null, BOLD, new Border(DOTTED, DOTTED, top, bottom), null)
                        .cell(2, r, t, null, new Border(DOTTED, NONE, top, bottom), LEFT)
                        .cell(3, r, Formula.sum(new Range(4, r, lastColumn, r)), BOLD, new Border(DOTTED, THIN, top, bottom), null);
                c = 4;
                for (final var tc : tcs) {
                    int count = count(tc, b, t, SEND, pAll);
                    pWorksheet.cell(c, r, count != 0 ? count : null, null, new Border(THIN, NONE, top, bottom), null, count != 0 ? (pAll ? "Results" : "Content") : null);
                    count = count(tc, b, t, PRESENT, pAll);
                    pWorksheet.cell(c + 1, r, count != 0 ? count : null, null, new Border(NONE, NONE, top, bottom), null, count != 0 ? (pAll ? "Results" : "Content") : null);
                    count = count(tc, b, t, ABSENT, pAll);
                    pWorksheet.cell(c + 2, r, count != 0 ? count : null, null, new Border(NONE, NONE, top, bottom), null, count != 0 ? (pAll ? "Results" : "Content") : null);
                    count = count(tc, b, t, TODO, pAll);
                    pWorksheet.cell(c + 3, r, count != 0 ? count : null, null, new Border(NONE, THIN, top, bottom), null, count != 0 ? (pAll ? "Results" : "Content") : null);
                    c += 4;
                }
                r++;
            }
        }
        c = 4;
        r--;
        for (final var _ : tcs) {
            pWorksheet
                    .cell(c, startRow - 1, Formula.sum(new Range(c, startRow, c, r)), BOLD, new Border(THIN, NONE, NONE, THIN), null)
                    .cell(c + 1, startRow - 1, Formula.sum(new Range(c + 1, startRow, c + 1, r)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                    .cell(c + 2, startRow - 1, Formula.sum(new Range(c + 2, startRow, c + 2, r)), BOLD, new Border(NONE, NONE, NONE, THIN), null)
                    .cell(c + 3, startRow - 1, Formula.sum(new Range(c + 3, startRow, c + 3, r)), BOLD, new Border(NONE, THIN, NONE, THIN), null);
            c += 4;
        }
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
}
