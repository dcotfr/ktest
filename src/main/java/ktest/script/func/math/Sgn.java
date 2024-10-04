package ktest.script.func.math;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;

import static java.lang.Math.signum;
import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Sgn extends Func {
    protected Sgn() {
        super("sgn", new FuncDoc(MATH, "-9.63", "-1", "Returns the sign of the number (-1, 0 or 1)."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int(Double.valueOf(signum(((Number) params[0]).doubleValue())).longValue());
    }
}
