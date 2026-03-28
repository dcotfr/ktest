package ktest.script.token;

import ktest.script.Context;
import ktest.script.ScriptException;

final class Else extends Token<Character> {
    Else() {
        super(8, ':');
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        throw new ScriptException(pStatement.syntaxErrorMessage("unexpected ':'", idx));
    }
}
