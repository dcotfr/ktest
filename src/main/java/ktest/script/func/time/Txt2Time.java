package ktest.script.func.time;

import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static ktest.script.func.FuncType.TIME;

@ApplicationScoped
public class Txt2Time extends Func {
    protected Txt2Time() {
        super("txt2time", new FuncDoc(TIME, "\"yyyy/MM/dd\", \"2024/07/17\"", "1721174400000", "Returns the timestamp of a formatted date string."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, String.class);
        try {
            final var format = new SimpleDateFormat((String) params[0]);
            return new Int(format.parse((String) params[1]).getTime());
        } catch (final NullPointerException | IllegalArgumentException e) {
            throw new ScriptException("Invalid date/time format in " + command() + ": " + params[0]);
        } catch (final ParseException e) {
            throw new ScriptException("Invalid date string in " + command() + ": " + params[1]);
        }
    }
}