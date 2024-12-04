package ktest.core;

import java.time.Duration;

public final class LogTab {
    private final boolean showThread;

    public LogTab(final boolean pShowThread) {
        showThread = pShowThread;
    }

    private int tab = 0;

    public void inc() {
        tab += 2;
    }

    public void dec() {
        tab -= 2;
    }

    public String tab(final String pColor) {
        return (showThread ? Thread.currentThread().threadId() + " " : "") + " ".repeat(tab) + pColor;
    }

    public static String secondsToHuman(final double pSeconds) {
        return Duration.ofMillis((long) (pSeconds * 1000)).toString().substring(2).toLowerCase();
    }
}
