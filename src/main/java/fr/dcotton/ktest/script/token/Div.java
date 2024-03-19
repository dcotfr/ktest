package fr.dcotton.ktest.script.token;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;

final class Div extends Token<Character> {
    Div() {
        super(3, '/');
    }

    @Override
    Token eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var right = ((Number) pStatement.evalAsNumAt(pContext, idx + 1, false).value()).doubleValue();
        if (right == 0.0) {
            throw new ScriptException(pStatement.syntaxErrorMessage("divide by zero", idx + 1));
        }
        final var left = ((Number) pStatement.evalAsNumAt(pContext, idx - 1, false).value()).doubleValue();
        pStatement.replace3With(idx, new Flt(left / right));
        return pStatement;
    }
}
