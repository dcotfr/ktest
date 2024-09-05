package ktest.script.func.text;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class Left extends Func {
    protected Left() {
        super("left", new FuncDoc(TEXT, "\"Sample\", 3", "\"Sam\"", "Returns the x first characters of a string."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, Number.class);
        final var str = (String) params[0];
        final var nb = ((Number) params[1]).intValue();
        return new Txt(str.substring(0, Math.max(0, Math.min(nb, str.length()))));
    }
}
