package ktest.script.token;

import ktest.script.Context;
import ktest.script.ScriptException;

final class Var extends Token<String> {
    Var(final String pValue) {
        super(6, pValue);
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var v = pContext.variable(value());
        if (v != null) {
            pStatement.replace1With(idx, v);
        } else {
            throw new ScriptException(pStatement.syntaxErrorMessage("unknown variable", idx));
        }
        return pStatement;
    }
}
