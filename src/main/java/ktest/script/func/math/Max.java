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
public class Max extends Func {
    protected Max() {
        super("max", new FuncDoc(MATH, "5, -2", "5", "Returns the maximal value of 2 numbers."));
    }

    @Override
    public Num apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class, Number.class);
        final var n1 = (Number) params[0];
        final var n2 = (Number) params[1];
        final var max = Math.max(n1.doubleValue(), n2.doubleValue());
        if (n1 instanceof Long && n2 instanceof Long) {
            return new Int(Double.valueOf(max).longValue());
        }
        return new Flt(max);
    }
}
