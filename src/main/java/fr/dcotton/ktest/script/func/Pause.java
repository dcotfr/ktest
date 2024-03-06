package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Token;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Pause extends Func {
    protected Pause() {
        super("pause", new FuncDoc("3000", "", "Pause treatment during provided milliseconds."));
    }

    @Override
    public Token apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Double.class);
        try {
            Thread.sleep(((Double) params[0]).longValue());
            return new Txt("");
        } catch (final InterruptedException e) {
            throw new RuntimeException(command() + " interrupted.", e);
        }
    }
}
