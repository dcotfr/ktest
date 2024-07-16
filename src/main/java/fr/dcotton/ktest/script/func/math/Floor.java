package fr.dcotton.ktest.script.func.math;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Floor extends Func {
    protected Floor() {
        super("floor", new FuncDoc("3.14", "3", "Returns the greatest integer value <= to given number."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int(Double.valueOf(Math.floor(((Number) params[0]).doubleValue())).longValue());
    }
}
