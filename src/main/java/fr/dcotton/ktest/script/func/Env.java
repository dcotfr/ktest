package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Env extends Func {
    protected Env() {
        super("env", new FuncDoc("\"PATH\"", "\"C:\\Windows\\...\"", "Returns the value of an ENV variable."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        final var res = System.getenv((String) params[0]);
        return new Txt(res != null ? res : "");
    }
}
