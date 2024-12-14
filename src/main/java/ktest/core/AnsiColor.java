package ktest.core;

// Pour Windows cf https://ss64.com/nt/syntax-ansi.html
// Pour Linux cf https://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
public final class AnsiColor {
    public static final String WHITE = "\u001B[97m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String LIGHTGRAY = "\u001B[37m";
    public static final String DARKGRAY = "\u001B[30m";
    public static final String BRIGHTYELLOW = "\u001B[93m";

    private AnsiColor() {
    }
}
