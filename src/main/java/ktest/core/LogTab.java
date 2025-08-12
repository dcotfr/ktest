package ktest.core;

import java.time.Duration;

public final class LogTab {
    private final boolean showThread;

    public LogTab(final boolean pShowThread) {
        showThread = pShowThread;
    }

    private int tab;

    public void inc() {
        tab += 2;
    }

    public void dec() {
        tab -= 2;
    }

    public static String tab(final String pColor, final int pTab, final boolean pShowThread) {
        return (pShowThread ? Thread.currentThread().threadId() + " " : "") + " ".repeat(pTab) + pColor;
    }

    public String tab(final String pColor) {
        return tab(pColor, tab, showThread);
    }

    public static String secondsToHuman(final double pSeconds) {
        return Duration.ofMillis((long) (pSeconds * 1000)).toString().substring(2).toLowerCase();
    }
}
