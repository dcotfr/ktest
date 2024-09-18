package ktest.script.token;

import ktest.script.Context;
import ktest.script.ScriptException;

final class Fun extends Token<String> {
    private final Stm param;

    Fun(final String pValue, final Stm pParam) {
        super(5, pValue);
        param = pParam;
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var f = pContext.function(value());
        if (f != null) {
            pStatement.replace1With(idx, f.apply(pContext, param));
        } else {
            throw new ScriptException(pStatement.syntaxErrorMessage("unknown function", idx));
        }
        return pStatement;
    }

    @Override
    public String toString() {
        return super.toString() + '(' + param.toString() + ')';
    }
}
