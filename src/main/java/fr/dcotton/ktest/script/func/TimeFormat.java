package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Token;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.SimpleDateFormat;
import java.util.Date;

@ApplicationScoped
public class TimeFormat extends Func {
    protected TimeFormat() {
        super("time.format", new FuncDoc("\"yyyy-MM-dd HH:mm:ss\", 1708854821321", "\"2024-02-25 10:53:41\"", "Returns the formatted date/string of a timestamp at current TimeZone."));
    }

    @Override
    public Token apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, Number.class);
        try {
            final var format = new SimpleDateFormat((String) params[0]);
            return new Txt(format.format(new Date(((Number) params[1]).longValue())));
        } catch (final NullPointerException | IllegalArgumentException e) {
            throw new ScriptException("Invalid date/time format in " + command() + ": " + params[0]);
        }
    }
}
