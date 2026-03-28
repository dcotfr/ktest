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

        var onTrueStm = (Stm) pStatement.value().get(idx + 1);
        Stm onFalseStm = null;
        if (onTrueStm.value().size() == 3 && onTrueStm.value().get(1) instanceof Else) {
            onFalseStm = (Stm) onTrueStm.value().get(2);
            onTrueStm = (Stm) onTrueStm.value().get(0);
        }
        if (Math.abs(condition.value().doubleValue() - 1.0) < 10e-8) {
            pStatement.replace3With(idx, onTrueStm.eval(pContext, onTrueStm));
        } else {
            pStatement.replace3With(idx, onFalseStm != null ? onFalseStm.eval(pContext, onFalseStm) : condition);
        }
        return pStatement;
    }
}
