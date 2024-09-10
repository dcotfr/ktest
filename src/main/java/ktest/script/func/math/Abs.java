package ktest.script.func.math;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Flt;
import ktest.script.token.Int;
import ktest.script.token.Num;
import ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Abs extends Func {
    protected Abs() {
        super("abs", new FuncDoc(MATH, "-3.14", "3.14", "Returns the absolute value of a number."));
    }

    @Override
    public Num apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        final var number = (Number) params[0];
        if (number instanceof Long) {
            return new Int(Math.abs(number.longValue()));
        }
        return new Flt(Math.abs(number.doubleValue()));
    }
}