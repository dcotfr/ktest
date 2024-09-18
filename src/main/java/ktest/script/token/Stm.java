package ktest.script.token;

import ktest.script.Context;
import ktest.script.ScriptException;

import java.util.ArrayList;
import java.util.List;

public final class Stm extends Token<List<Token<?>>> {
    private final Stm parent;

    Stm(final Stm pParent) {
        super(6, new ArrayList<>());
        parent = pParent;
    }

    Stm parent() {
        return parent;
    }

    void add(final Token<?> pToken) {
        if (pToken instanceof Stm stm && stm.value().isEmpty()) {
            return;
        }
        value().add(pToken);
    }

    @Override
    Token<?> eval(final Context pContext, final Stm pStatement) {
        while (true) {
            int priority = 0;
            Token<?> toEval = null;
            for (final var t : value()) {
                if (t.priority() > priority) {
                    priority = t.priority();
                    toEval = t;
                }
            }
            if (toEval == null) {
                break;
            }
            if (toEval instanceof Stm stm && stm.value().size() == 1) {
                value().set(value().indexOf(toEval), stm.value().getFirst());
            } else {
                toEval.eval(pContext, this);
            }
        }

        return value().getFirst();
    }

    public Object evalValue(final Context pContext) {
        return eval(pContext, this).value();
    }

    Token<?> evalAt(final Context pContext, final int pIndex) {
        if (pIndex < 0 || pIndex >= value().size()) {
            throw new ScriptException(syntaxErrorMessage("a token was expected", pIndex));
        }
        return value().get(pIndex).eval(pContext, this);
    }

    Num<?> evalAsNumAt(final Context pContext, final int pIndex, boolean pNullIfOutOfBoundError) {
        if (pIndex < 0 || pIndex >= value().size()) {
            if (pNullIfOutOfBoundError) {
                return null;
            }
        } else {
            final var parentToken = value().get(pIndex);
            Token<?> token = null;
            if (!(parentToken instanceof Let)) {
                token = parentToken.eval(pContext, this);
            } else if (pNullIfOutOfBoundError) {
                return null;
            }
            if (token instanceof Num<?> num) {
                return num;
            }
        }
        throw new ScriptException(syntaxErrorMessage("a number was expected", pIndex));
    }

    void group() {
        final var compact = new Stm(parent());
        compact.value().addAll(value());
        value().clear();
        value().add(compact);
    }

    void replace1With(final int pIndex, final Token<?> pToken) {
        if (pToken != null) {
            value().set(pIndex, pToken);
        } else {
            value().remove(pIndex);
        }
    }

    void replace2With(final int pIndex, final Token<?> pToken) {
        replace1With(pIndex + 1, null);
        replace1With(pIndex, pToken);
    }

    void replace3With(final int pIndex, final Token<?> pToken) {
        replace2With(pIndex, pToken);
        replace1With(pIndex - 1, null);
    }

    String syntaxErrorMessage(final String pMessage, final int pIndex) {
        final var res = new StringBuilder("Syntax error: ");
        res.append(pMessage).append(" in ");
        for (var i = 0; i < value().size(); i++) {
            res.append(i == pIndex ? ">>>" : "").append(value().get(i).value()).append(i == pIndex ? "<<<" : "");
        }
        return res.toString();
    }
}
