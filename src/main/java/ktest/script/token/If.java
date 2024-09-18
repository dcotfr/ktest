package ktest.script.token;

import ktest.script.Context;

final class If extends Token<Character> {
    If() {
        super(8, '?');
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var condition = pStatement.evalAsNumAt(pContext, idx - 1, false);
        pStatement.replace3With(idx, Math.abs(condition.value().doubleValue() - 1.0) < 10e-8
                ? pStatement.evalAt(pContext, idx + 1)
                : condition);
        return pStatement;
    }
}
