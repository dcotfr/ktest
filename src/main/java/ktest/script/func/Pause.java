package ktest.script.func;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Pause extends Func {
    protected Pause() {
        super("pause", new FuncDoc(MISC, "3000", "", "Pause treatment during provided milliseconds."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        if (pContext.pauseDisabled()) {
            return new Txt("");
        }
        final var params = extractParam(pContext, pParam, Number.class);
        try {
            Thread.sleep(((Number) params[0]).longValue());
            return new Txt("");
        } catch (final InterruptedException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}
