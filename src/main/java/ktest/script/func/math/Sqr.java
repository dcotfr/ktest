package ktest.script.func.math;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Flt;
import ktest.script.token.Stm;

import static java.lang.Math.sqrt;
import static ktest.script.func.FuncType.MATH;

@ApplicationScoped
public class Sqr extends Func {
    protected Sqr() {
        super("sqr", new FuncDoc(MATH, "2", "1.4142135623730951", "Returns the square root of the number."));
    }

    @Override
    public Flt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        final var value = ((Number) params[0]).doubleValue();
        if (value < 0) {
            throw new ScriptException("A positive number is expected in " + command() + ": " + value + " found.");
        }
        return new Flt(sqrt(value));
    }
}
