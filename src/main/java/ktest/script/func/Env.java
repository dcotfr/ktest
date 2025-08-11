package ktest.script.func;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Env extends Func {
    protected Env() {
        super("env", new FuncDoc(MISC, "\"SHELL\"", "\"/bin/bash\"", "Returns the value of an ENV variable."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        final var res = System.getenv((String) params[0]);
        return new Txt(res != null ? res : "");
    }
}
