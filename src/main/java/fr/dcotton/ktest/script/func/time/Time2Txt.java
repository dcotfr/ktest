package fr.dcotton.ktest.script.func.time;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.SimpleDateFormat;
import java.util.Date;

@ApplicationScoped
public class Time2Txt extends Func {
    protected Time2Txt() {
        super("time2txt", new FuncDoc("\"yyyy-MM-dd HH:mm:ss\", 1708854821321", "\"2024-02-25 10:53:41\"", "Returns the formatted date/string of a timestamp at current TimeZone."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, Number.class);
        try {
            final var format = new SimpleDateFormat((String) params[0]);
            return new Txt(format.format(new Date(((Number) params[1]).longValue())));
        } catch (final NullPointerException | IllegalArgumentException e) {
            throw new ScriptException("Invalid date/time format in " + command() + ": " + params[0]);
        }
    }
}