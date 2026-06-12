package ktest.script.token;

final class LineBuffer {
    private final String line;
    private int pos;

    LineBuffer(final String pLine) {
        line = pLine;
    }

    boolean hasNext() {
        return pos < line.length();
    }

    char peek() {
        return hasNext() ? line.charAt(pos) : '\0';
    }

    int pos() {
        return pos;
    }

    char pop() {
        final var res = peek();
        pos++;
        return res;
    }

    boolean match(final char c) {
        if (peek() == c) {
            pos++;
            return true;
        }
        return false;
    }

    String remaining() {
        return line.substring(pos);
    }

    void flush() {
        pos = line.length();
    }

    String substring(int begin, int end) {
        return line.substring(begin, end);
    }
}
