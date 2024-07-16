package fr.dcotton.ktest.script.func.math;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Round extends Func {
    protected Round() {
        super("round", new FuncDoc("2.43", "2", "Returns the nearest integer, rounding half away from zero."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int(Double.valueOf(Math.round(((Number) params[0]).doubleValue())).longValue());
    }
}
