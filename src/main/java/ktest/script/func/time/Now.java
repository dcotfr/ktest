package ktest.script.func.time;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.TIME;

@ApplicationScoped
public class Now extends Func {
    protected Now() {
        super("now", new FuncDoc(TIME, "", "1708808432990", "Returns the current time in millis."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        extractParam(pContext, pParam);
        return new Int(System.currentTimeMillis());
    }
}
