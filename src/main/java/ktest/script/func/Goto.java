package ktest.script.func;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Goto extends Func {
    protected Goto() {
        super("goto", new FuncDoc(MISC, "\"NameOfStep\"", "", "Jump and continue to named Step."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        throw new GotoException(params[0].toString());
    }
}
