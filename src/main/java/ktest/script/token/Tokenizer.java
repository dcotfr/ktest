package ktest.script.token;

import ktest.script.ScriptException;

import java.util.ArrayList;
import java.util.List;

public final class Tokenizer {
    public List<Stm> tokenize(final String pLine) {
        final var stms = new ArrayList<Stm>();
        var stm = new Stm(null);
        stms.add(stm);
        final var buf = new LineBuffer(pLine);
        while (buf.hasNext()) {
            if (buf.isLParent()) {
                buf.pop();
                stm = new Stm(stm);
            } else if (buf.isRParent()) {
                buf.pop();
                final var parent = stm.parent();
                if (parent == null) {
                    error("Unexpected right parenthesis", pLine, buf.pos());
                }
                parent.add(stm);
                stm = parent;
            } else if ("=+-*/".indexOf(buf.current()) >= 0) {
                switch (buf.pop()) {
                    case '=' -> {
                        if (buf.current() == '=') {
                            buf.pop();
                            stm.add(new Eq());
                        } else if (stm.parent() == null && stm.value().size() == 1 && stm.value().getFirst() instanceof Var var) {
                            stm.replace1With(0, new Let(var.value()));
                        } else {
                            error("Unexpected affectation", pLine, buf.pos());
                        }
                    }
                    case '+' -> stm.add(new Add());
                    case '-' -> stm.add(new Sub());
                    case '*' -> stm.add(new Mul());
                    case '/' -> stm.add(new Div());
                }
            } else if (buf.current() == '!') {
                buf.pop();
                if (buf.current() != '=') {
                    error("Unexpected character", pLine, buf.pos());
                }
                buf.pop();
                stm.add(new Ne());
            } else if (buf.current() == '<') {
                buf.pop();
                if (buf.current() != '=') {
                    stm.add(new Lt());
                } else {
                    buf.pop();
                    stm.add(new Le());
                }
            } else if (buf.current() == '>') {
                buf.pop();
                if (buf.current() != '=') {
                    stm.add(new Gt());
                } else {
                    buf.pop();
                    stm.add(new Ge());
                }
            } else if (buf.current() == '?') {
                buf.pop();
                stm.group();
                stm.add(new If());
                stm.add(tokenize(buf.readEndOfBuffer()).getFirst());
            } else if (buf.current() == ';') {
                buf.pop();
                stm = new Stm(null);
                stms.add(stm);
            } else if (buf.isDigit()) {
                stm.add(buf.readNum());
            } else if (buf.isDQuote()) {
                buf.pop();
                final var str = buf.readTxt();
                if (str == null) {
                    error("Double quote not matching", pLine, buf.pos());
                }
                stm.add(str);
            } else if (buf.isLetter() || buf.isUnderscore()) {
                final var ident = buf.readIdentifier();
                stm.add(buf.isLParent() ? new Fun(ident.value(), tokenize(buf.readParam()).getFirst()) : ident);
            } else if (buf.current() == ',') {
                buf.pop();
                final var parent = stm.parent();
                if (parent == null) {
                    error("Unexpected comma", pLine, buf.pos());
                }
                parent.add(stm);
                stm = new Stm(parent);
            } else if (buf.isIgnorable()) {
                buf.pop();
            } else {
                buf.pop();
                error("Unexpected char", pLine, buf.pos());
            }
        }

        if (stm.parent() != null) {
            error("Parenthesis not matching", pLine, buf.pos());
        }
        return stms;
    }

    private void error(final String pMessage, final String pLine, final int pPos) {
        throw new ScriptException(pMessage + '\n' + pLine + '\n' + "-".repeat(pPos - 1) + '^' + '\n');
    }
}
