package ktest.script.token;

import ktest.script.ScriptException;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.*;

public final class Tokenizer {
    public List<Stm> tokenize(final String pLine) {
        final var stms = new ArrayList<Stm>();
        var stm = new Stm(null);
        stms.add(stm);
        final var buf = new LineBuffer(pLine);
        while (buf.hasNext()) {
            stm = handleToken(stm, pLine, buf, stms);
        }
        if (stm.parent() != null) {
            error("Parenthesis not matching", pLine, buf.pos());
        }
        return stms;
    }

    private Stm handleToken(final Stm pStm, final String pLine, final LineBuffer pBuffer, final List<Stm> pStms) {
        if (pBuffer.match('(')) {
            return handleOpenParen(pStm);
        } else if (pBuffer.match(')')) {
            return handleCloseParen(pStm, pLine, pBuffer);
        } else if ("=+-*/".indexOf(pBuffer.peek()) >= 0) {
            handleOperator(pStm, pLine, pBuffer);
        } else if (pBuffer.match('!')) {
            handleNotEqual(pStm, pLine, pBuffer);
        } else if (pBuffer.match('<')) {
            handleLessThan(pStm, pBuffer);
        } else if (pBuffer.match('>')) {
            handleGreaterThan(pStm, pBuffer);
        } else if (pBuffer.match('?')) {
            handleIf(pStm, pBuffer);
        } else if (pBuffer.match(':')) {
            handleElse(pStm, pBuffer);
        } else if (pBuffer.match(';')) {
            return handleSemicolon(pStms);
        } else if (isDigit(pBuffer.peek())) {
            pStm.add(readNum(pBuffer));
        } else if (pBuffer.match('"')) {
            handleString(pStm, pLine, pBuffer);
        } else if (isLetter(pBuffer.peek()) || pBuffer.peek() == '_') {
            handleIdentifier(pStm, pBuffer);
        } else if (pBuffer.match(',')) {
            return handleComma(pStm, pLine, pBuffer);
        } else if (isWhitespace(pBuffer.peek())) {
            pBuffer.pop();
        } else {
            pBuffer.pop();
            error("Unexpected char", pLine, pBuffer.pos());
        }
        return pStm;
    }

    private Stm handleOpenParen(final Stm pStm) {
        return new Stm(pStm);
    }

    private Stm handleCloseParen(final Stm pStm, final String pLine, final LineBuffer pBuffer) {
        final var parent = pStm.parent();
        if (parent == null) {
            error("Unexpected right parenthesis", pLine, pBuffer.pos());
        }
        parent.add(pStm);
        return parent;
    }

    private void handleOperator(final Stm pStm, final String pLine, final LineBuffer pBuffer) {
        switch (pBuffer.pop()) {
            case '=' -> {
                if (pBuffer.peek() == '=') {
                    pBuffer.pop();
                    pStm.add(new Eq());
                } else if (pStm.parent() == null && pStm.value().size() == 1 && pStm.value().getFirst() instanceof final Var v) {
                    pStm.replace1With(0, new Let(v.value()));
                } else {
                    error("Unexpected affectation", pLine, pBuffer.pos());
                }
            }
            case '+' -> pStm.add(new Add());
            case '-' -> pStm.add(new Sub());
            case '*' -> pStm.add(new Mul());
            case '/' -> pStm.add(new Div());
        }
    }

    private void handleNotEqual(final Stm pStm, final String pLine, final LineBuffer pBuffer) {
        if (!pBuffer.match('=')) {
            error("Unexpected character", pLine, pBuffer.pos());
        }
        pStm.add(new Ne());
    }

    private void handleLessThan(final Stm pStm, final LineBuffer pBuffer) {
        pStm.add(pBuffer.match('=') ? new Le() : new Lt());
    }

    private void handleGreaterThan(final Stm pStm, final LineBuffer pBuffer) {
        pStm.add(pBuffer.match('=') ? new Ge() : new Gt());
    }

    private void handleIf(final Stm pStm, final LineBuffer pBuffer) {
        pStm.group();
        pStm.add(new If());
        pStm.add(tokenize(pBuffer.remaining()).getFirst());
        pBuffer.flush();
    }

    private void handleElse(final Stm pStm, final LineBuffer pBuffer) {
        pStm.group();
        pStm.add(new Else());
        pStm.add(tokenize(pBuffer.remaining()).getFirst());
        pBuffer.flush();
    }

    private Stm handleSemicolon(final List<Stm> pStms) {
        final var stm = new Stm(null);
        pStms.add(stm);
        return stm;
    }

    private void handleString(final Stm pStm, final String pLine, final LineBuffer pBuffer) {
        final var str = readTxt(pBuffer);
        if (str == null) {
            error("Double quote not matching", pLine, pBuffer.pos());
        }
        pStm.add(str);
    }

    private void handleIdentifier(final Stm pStm, final LineBuffer pBuffer) {
        final var ident = readIdentifier(pBuffer);
        pStm.add(pBuffer.peek() == '(' ? new Fun(ident.value(), tokenize(readParam(pBuffer)).getFirst()) : ident);
    }

    private Stm handleComma(final Stm pStm, final String pLine, final LineBuffer pBuffer) {
        final var parent = pStm.parent();
        if (parent == null) {
            error("Unexpected comma", pLine, pBuffer.pos());
        }
        parent.add(pStm);
        return new Stm(parent);
    }

    private Num<? extends Number> readNum(final LineBuffer pBuffer) {
        final StringBuilder raw = new StringBuilder();
        while (isDigit(pBuffer.peek())) {
            raw.append(pBuffer.pop());
        }
        if (pBuffer.peek() == '.') {
            do {
                raw.append(pBuffer.pop());
            } while (isDigit(pBuffer.peek()));
            return new Flt(Double.parseDouble(raw.toString()));
        }
        return new Int(Long.parseLong(raw.toString()));
    }

    private Var readIdentifier(final LineBuffer pBuffer) {
        final StringBuilder raw = new StringBuilder();
        char c;
        while ((c = pBuffer.peek()) != '\0' && (isAlphabetic(c) || isDigit(c) || c == '_' || c == '.')) {
            raw.append(pBuffer.pop());
        }
        return new Var(raw.toString());
    }

    private Txt readTxt(final LineBuffer pBuffer) {
        final StringBuilder res = new StringBuilder();
        while (pBuffer.hasNext() && pBuffer.peek() != '"') {
            res.append(pBuffer.pop());
        }
        return pBuffer.match('"') ? new Txt(res.toString()) : null;
    }

    private String readParam(final LineBuffer pBuffer) {
        final var start = pBuffer.pos();
        var depth = 0;
        while (pBuffer.hasNext()) {
            final char c = pBuffer.peek();
            if (c == '(') {
                depth++;
                pBuffer.pop();
            } else if (c == ')') {
                pBuffer.pop();
                depth--;
                if (depth == 0) {
                    break;
                }
            } else if (c == '"') {
                pBuffer.pop();
                readTxt(pBuffer);
            } else {
                pBuffer.pop();
            }
        }
        return pBuffer.substring(start, pBuffer.pos());
    }

    private void error(final String pMessage, final String pLine, final int pPos) {
        throw new ScriptException(pMessage + '\n' + pLine + '\n' + "-".repeat(pPos - 1) + '^' + '\n');
    }
}
