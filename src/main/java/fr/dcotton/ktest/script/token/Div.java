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
        final var right = pStatement.evalAsNumAt(pContext, idx + 1, false).value();
        if (right == 0.0) {
            throw new ScriptException(pStatement.syntaxErrorMessage("divide by zero", idx + 1));
        }
        final var left = pStatement.evalAsNumAt(pContext, idx - 1, false).value();
        pStatement.replace3With(idx, new Num(left / right));
        return pStatement;
    }
}
