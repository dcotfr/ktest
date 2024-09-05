package ktest.core;

// Pour Windows cf https://ss64.com/nt/syntax-ansi.html
// Pour Linux cf https://stackoverflow.com/questions/5947742/how-to-change-the-output-color-of-echo-in-linux
public interface AnsiColor {
    String WHITE = "\u001B[97m";
    String CYAN = "\u001B[36m";
    String MAGENTA = "\u001B[35m";
    String BLUE = "\u001B[34m";
    String YELLOW = "\u001B[33m";
    String GREEN = "\u001B[32m";
    String RED = "\u001B[31m";
    String LIGHTGRAY = "\u001B[37m";
    String DARKGRAY = "\u001B[30m";
    String BRIGHTYELLOW = "\u001B[93m";
}
