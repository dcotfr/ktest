package fr.dcotton.ktest.script.func.math;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Flt;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Num;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Min extends Func {
    protected Min() {
        super("min", new FuncDoc("5, -2", "-2", "Returns the minimal value of 2 numbers."));
    }

    @Override
    public Num apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class, Number.class);
        final var n1 = (Number) params[0];
        final var n2 = (Number) params[1];
        final var min = Math.min(n1.doubleValue(), n2.doubleValue());
        if (n1 instanceof Long && n2 instanceof Long) {
            return new Int(Double.valueOf(min).longValue());
        }
        return new Flt(min);
    }
}
