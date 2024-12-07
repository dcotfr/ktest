package ktest.script.func.math;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;

import static java.lang.Math.floor;
import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Floor extends Func {
    protected Floor() {
        super("floor", new FuncDoc(MATH, "3.14", "3", "Returns the greatest integer value <= to given number."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int((long) floor(((Number) params[0]).doubleValue()));
    }
}
