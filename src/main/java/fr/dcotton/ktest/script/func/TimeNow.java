package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TimeNow extends Func {
    protected TimeNow() {
        super("time.now", new FuncDoc("", "1708808432990", "Returns the current time in millis."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        extractParam(pContext, pParam);
        return new Int(System.currentTimeMillis());
    }
}
