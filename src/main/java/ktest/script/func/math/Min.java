package ktest.script.func.math;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Flt;
import ktest.script.token.Int;
import ktest.script.token.Num;
import ktest.script.token.Stm;

import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Min extends Func {
    protected Min() {
        super("min", new FuncDoc(MATH, "5, -2, 0", "-2", "Returns the minimal value of 1 or more numbers."));
    }

    @Override
    public Num<? extends Number> apply(final Context pContext, final Stm pParam) {
        final var params = extractUnboundParams(pContext, pParam, Number.class);
        if (params.length == 0) {
            throw new ScriptException("At least one number argument required.");
        }
        boolean isLongOnly = true;
        var min = Double.POSITIVE_INFINITY;
        for (final var p : params) {
            final var n = (Number) p;
            if (n instanceof Double) {
                isLongOnly = false;
            }
            if (n.doubleValue() < min) {
                min = n.doubleValue();
            }
        }
        if (isLongOnly) {
            return new Int((long) min);
        }
        return new Flt(min);
    }
}
