package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MathCeil extends Func {
    protected MathCeil() {
        super("math.ceil", new FuncDoc("3.14", "4", "Returns the least integer value >= to given number."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Int(Double.valueOf(Math.ceil(((Number) params[0]).doubleValue())).longValue());
    }
}
