package fr.dcotton.ktest.script.func.time;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Int;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@ApplicationScoped
public class Txt2Time extends Func {
    protected Txt2Time() {
        super("txt2time", new FuncDoc("\"yyyy/MM/dd\", \"2024/07/17\"", "1721174400000", "Returns the timestamp of a formatted date string."));
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