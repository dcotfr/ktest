package ktest.script.func;

import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Pause extends Func {
    protected Pause() {
        super("pause", new FuncDoc(MISC, "3000", "", "Pause treatment during provided milliseconds."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        try {
            Thread.sleep(((Number) params[0]).longValue());
            return new Txt("");
        } catch (final InterruptedException e) {
            throw new ScriptException(STR."\{command()} interrupted.", e);
        }
    }
}
