package fr.dcotton.ktest.script.token;

final class LineBuffer {
    private final char[] chars;
    private int pos;

    LineBuffer(final String pLine) {
        chars = pLine.toCharArray();
    }

    boolean hasNext() {
        return pos < chars.length;
    }

    char current() {
        return hasNext() ? chars[pos] : '\0';
    }

    int pos() {
        return pos;
    }

    boolean isDigit() {
        final var c = current();
        return c >= '0' && c <= '9';
    }

    boolean isLParent() {
        return current() == '(';
    }

    boolean isRParent() {
        return current() == ')';
    }

    boolean isIgnorable() {
        final var c = current();
        return c == ' ' || c == '\t' || c == '\n';
    }

    private boolean isDot() {
        return current() == '.';
    }

    boolean isLetter() {
        final var c = current();
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    boolean isUnderscore() {
        return current() == '_';
    }

    boolean isDQuote() {
        return current() == '"';
    }

    char pop() {
        final var res = current();
        pos++;
        return res;
    }

    Num readNumber() {
        final StringBuilder raw = new StringBuilder();
        while (isDigit()) {
            raw.append(pop());
        }
        if (isDot()) {
            raw.append(pop());
            while (isDigit()) {
                raw.append(pop());
            }
            return new Flt(Double.valueOf(raw.toString()));
        }
        return new Int(Long.valueOf(raw.toString()));
    }

    Var readIdentifier() {
        final StringBuilder raw = new StringBuilder();
        while (isLetter() || isDigit() || isUnderscore() || isDot()) {
            raw.append(pop());
        }
        return new Var(raw.toString());
    }

    Txt readString() {
        final StringBuilder res = new StringBuilder();
        while (hasNext() && !isDQuote()) {
            res.append(pop());
        }
        if (hasNext()) {
            pop();
            return new Txt(res.toString());
        }
        return null;
    }

    String readParam() {
        final StringBuilder res = new StringBuilder();
        final var subBuf = new LineBuffer(new String(chars).substring(pos));
        if (isLParent()) {
            int parenthesisDepth = 0;
            while (subBuf.hasNext()) {
                if (subBuf.isRParent()) {
                    subBuf.pop();
                    parenthesisDepth--;
                    if (parenthesisDepth == 0) {
                        break;
                    }
                } else if (subBuf.isLParent()) {
                    subBuf.pop();
                    parenthesisDepth++;
                } else if (subBuf.isDQuote()) {
                    subBuf.pop();
                    subBuf.readString();
                } else {
                    subBuf.pop();
                }
            }
            res.append(subBuf.chars, 0, subBuf.pos);
        }
        pos += subBuf.pos;
        return res.toString();
    }
}
