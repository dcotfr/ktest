package ktest.script.token;

import ktest.script.Context;

final class Sub extends Token<Character> {
    Sub() {
        super(3, '-');
    }

    @Override
    Stm eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var rTokValue = (Number) pStatement.evalAsNumAt(pContext, idx + 1, false).value();
        final var lTok = pStatement.evalAsNumAt(pContext, idx - 1, true);
        if (lTok != null) {
            final var lTokValue = (Number) lTok.value();
            pStatement.replace3With(idx, lTokValue instanceof Long && rTokValue instanceof Long
                    ? new Int(lTokValue.longValue() - rTokValue.longValue())
                    : new Flt(lTokValue.doubleValue() - rTokValue.doubleValue()));
        } else {
            pStatement.replace2With(idx, rTokValue instanceof Long
                    ? new Int(-rTokValue.longValue())
                    : new Flt(-rTokValue.doubleValue()));
        }
        return pStatement;
    }
}
