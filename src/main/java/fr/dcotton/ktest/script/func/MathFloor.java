package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MathFloor extends Func {
    protected MathFloor() {
        super("math.floor", new FuncDoc("3.14", "3", "Returns the greatest integer value <= to given number."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int(Double.valueOf(Math.floor(((Number) params[0]).doubleValue())).longValue());
    }
}
