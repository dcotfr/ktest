package ktest.script.func.math;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;

import static java.lang.Math.ceil;
import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Ceil extends Func {
    protected Ceil() {
        super("ceil", new FuncDoc(MATH, "3.14", "4", "Returns the least integer value >= to given number."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int((long) ceil(((Number) params[0]).doubleValue()));
    }
}
