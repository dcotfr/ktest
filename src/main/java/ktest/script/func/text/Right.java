package ktest.script.func.text;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class Right extends Func {
    protected Right() {
        super("right", new FuncDoc(TEXT, "\"Sample\", 3", "\"ple\"", "Returns the x last characters of a string."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, Number.class);
        final var str = (String) params[0];
        final var nb = ((Number) params[1]).intValue();
        return new Txt(str.substring(Math.max(0, str.length() - nb)));
    }
}
