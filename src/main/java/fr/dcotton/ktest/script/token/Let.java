package fr.dcotton.ktest.script.token;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;

final class Let extends Token<String> {
    Let(final String pValue) {
        super(1, pValue);
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var rTok = pStatement.evalAt(pContext, idx + 1);
        if (rTok != null && rTok.priority() < priority()) {
            pContext.variable(value(), rTok);
            pStatement.replace2With(idx, rTok);
        } else {
            throw new ScriptException(pStatement.syntaxErrorMessage("missing value to affect", idx));
        }
        return pStatement;
    }
}
