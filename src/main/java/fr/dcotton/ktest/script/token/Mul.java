package fr.dcotton.ktest.script.token;

import fr.dcotton.ktest.script.Context;

final class Mul extends Token<Character> {
    Mul() {
        super(3, '*');
    }

    @Override
    Token eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var left = pStatement.evalAsNumAt(pContext, idx - 1, false).value();
        final var right = pStatement.evalAsNumAt(pContext, idx + 1, false).value();
        pStatement.replace3With(idx, new Num(left * right));
        return pStatement;
    }
}
