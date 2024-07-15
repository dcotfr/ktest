package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Flt;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Num;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MathAbs extends Func {
    protected MathAbs() {
        super("math.abs", new FuncDoc("-3.14", "3.14", "Returns the absolute value of a number."));
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
