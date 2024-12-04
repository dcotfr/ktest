package ktest.core;

import org.junit.jupiter.api.Test;

import static ktest.core.LogTab.secondsToHuman;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LogTabTest {
    @Test
    void secondsToHumanTest() {
        assertEquals("0.001s", secondsToHuman(0.001));
        assertEquals("32.25s", secondsToHuman(32.25));
        assertEquals("6m12s", secondsToHuman(6 * 60 + 12));
        assertEquals("2h15m4.3s", secondsToHuman(2 * 60 * 60 + 15 * 60 + 4.3));
        assertEquals("88h27m54.987s", secondsToHuman(3 * 24 * 60 * 60 + 16 * 60 * 60 + 27 * 60 + 54.987));
    }
}
