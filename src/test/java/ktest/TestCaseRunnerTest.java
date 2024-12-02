package ktest;

import ktest.domain.TestCase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ktest.TestCaseRunner.filteredByTags;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestCaseRunnerTest {
    @Test
    void filteredByTagsTest() {
        final var testCases = List.of(new TestCase("TC1", List.of("t1", "t2", "t3", "t4"), null, null, null),
                new TestCase("TC2", List.of("t1", "t2", "t3"), null, null, null),
                new TestCase("TC3", List.of("t1", "t2"), null, null, null),
                new TestCase("TC4", List.of("t1"), null, null, null),
                new TestCase("TC5", List.of(""), null, null, null),
                new TestCase("TC6", null, null, null, null),
                new TestCase("TC7", List.of("t2", "t4"), null, null, null)
        );

        var res = filteredByTags(testCases, null);
        assertEquals(7, res.size());

        res = filteredByTags(testCases, "");
        assertEquals(7, res.size());

        res = filteredByTags(testCases, "t1");
        assertEquals(4, res.size());

        res = filteredByTags(testCases, "t2");
        assertEquals(4, res.size());

        res = filteredByTags(testCases, "t3");
        assertEquals(2, res.size());

        res = filteredByTags(testCases, "t2+t4");
        assertEquals(2, res.size());

        res = filteredByTags(testCases, "t2+t4,t3");
        assertEquals(3, res.size());

        res = filteredByTags(testCases, "t1, t2+t4");
        assertEquals(5, res.size());

        res = filteredByTags(testCases, "!t2");
        assertEquals(3, res.size());

        res = filteredByTags(testCases, "t1,!t3");
        assertEquals(2, res.size());
    }
}
