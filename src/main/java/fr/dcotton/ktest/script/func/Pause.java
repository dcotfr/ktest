package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Pause extends Func {
    protected Pause() {
        super("pause", new FuncDoc("3000", "", "Pause treatment during provided milliseconds."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        try {
            Thread.sleep(((Number) params[0]).longValue());
            return new Txt("");
        } catch (final InterruptedException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}
