package ktest.script.func.time;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import java.text.SimpleDateFormat;
import java.util.Date;

import static ktest.script.func.FuncType.TIME;

@ApplicationScoped
public class Time2Txt extends Func {
    protected Time2Txt() {
        super("time2txt", new FuncDoc(TIME, "\"yyyy-MM-dd HH:mm:ss\", 1708854821321", "\"2024-02-25 10:53:41\"", "Returns the formatted date/string of a timestamp at current TimeZone."));
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
