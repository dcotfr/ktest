package fr.dcotton.ktest.script.token;

import fr.dcotton.ktest.script.Context;

final class Sub extends Token<Character> {
    Sub() {
        super(2, '-');
    }

    @Override
    Token eval(final Context pContext, final Stm pStatement) {
        final var idx = pStatement.value().indexOf(this);
        final var rTok = pStatement.evalAsNumAt(pContext, idx + 1, false);
        final var lTok = pStatement.evalAsNumAt(pContext, idx - 1, true);
        if (lTok != null) {
            pStatement.replace3With(idx, new Num(lTok.value() - rTok.value()));
        } else {
            pStatement.replace2With(idx, new Num(-rTok.value()));
        }
        return pStatement;
    }
}
