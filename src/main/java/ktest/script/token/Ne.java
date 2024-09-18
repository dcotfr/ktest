package ktest.script.token;

import ktest.script.Context;

final class Ne extends Comp<String> {
    Ne() {
        super("!=");
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var leftValue = pStatement.evalAt(pContext, idx - 1).value().toString();
        final var rightValue = pStatement.evalAt(pContext, idx + 1).value().toString();
        pStatement.replace3With(idx, leftValue.equals(rightValue) ? FALSE_COMP : TRUE_COMP);
        return pStatement;
    }
}
