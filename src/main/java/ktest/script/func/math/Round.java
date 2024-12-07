package ktest.script.func.math;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;

import static java.lang.Math.round;
import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Round extends Func {
    protected Round() {
        super("round", new FuncDoc(MATH, "2.43", "2", "Returns the nearest integer, rounding half away from zero."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int(round(((Number) params[0]).doubleValue()));
    }
}
