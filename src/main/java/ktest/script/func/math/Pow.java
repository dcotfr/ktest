package ktest.script.func.math;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Flt;
import ktest.script.token.Num;
import ktest.script.token.Stm;

import static java.lang.Math.pow;
import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Pow extends Func {
    protected Pow() {
        super("pow", new FuncDoc(MATH, "2, 8", "256", "Returns the value of the 1st number raised to the power of the 2nd."));
    }

    @Override
    public Flt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class, Number.class);
        return new Flt(pow(((Number) params[0]).doubleValue(), ((Number) params[1]).doubleValue()));
    }
}
