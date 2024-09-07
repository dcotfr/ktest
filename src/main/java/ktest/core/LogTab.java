package ktest.core;

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
        return (showThread ? STR."\{Thread.currentThread().threadId()} " : "") + " ".repeat(tab) + pColor;
    }
}
