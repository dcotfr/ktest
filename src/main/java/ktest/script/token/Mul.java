package ktest.script.token;

import ktest.script.Context;

final class Mul extends Token<Character> {
    Mul() {
        super(3, '*');
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var leftValue = (Number) pStatement.evalAsNumAt(pContext, idx - 1, false).value();
        final var rightValue = (Number) pStatement.evalAsNumAt(pContext, idx + 1, false).value();
        pStatement.replace3With(idx, leftValue instanceof Long && rightValue instanceof Long ? new Int(leftValue.longValue() * rightValue.longValue()) : new Flt(leftValue.doubleValue() * rightValue.doubleValue()));
        return pStatement;
    }
}
