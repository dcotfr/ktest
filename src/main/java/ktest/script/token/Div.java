package ktest.script.token;

import ktest.script.Context;
import ktest.script.ScriptException;

final class Div extends Token<Character> {
    Div() {
        super(3, '/');
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var right = (pStatement.evalAsNumAt(pContext, idx + 1, false).value()).doubleValue();
        if (right == 0.0) {
            throw new ScriptException(pStatement.syntaxErrorMessage("divide by zero", idx + 1));
        }
        final var left = (pStatement.evalAsNumAt(pContext, idx - 1, false).value()).doubleValue();
        pStatement.replace3With(idx, new Flt(left / right));
        return pStatement;
    }
}
