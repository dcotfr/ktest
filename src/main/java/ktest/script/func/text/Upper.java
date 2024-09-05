package ktest.script.func.text;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class Upper extends Func {
    protected Upper() {
        super("upper", new FuncDoc(TEXT, "\"ToUpper\"", "\"TOUPPER\"", "Returns the upper cased string."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(((String) params[0]).toUpperCase());
    }
}
