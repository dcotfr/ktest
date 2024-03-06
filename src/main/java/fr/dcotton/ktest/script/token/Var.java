package fr.dcotton.ktest.script.token;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;

final class Var extends Token<String> {
    Var(final String pValue) {
        super(6, pValue);
    }

    @Override
    Token eval(final Context pContext, final Stm pStatement) {
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
