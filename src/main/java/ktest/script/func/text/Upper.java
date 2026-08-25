package ktest.script.func.text;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class Upper extends Func {
    protected Upper() {
        super("upper", new FuncDoc(TEXT, "\"ToUpper\"", "\"TOUPPER\"", "Returns the upper cased string.",
                "DSL Function that accepts a string as input and returns the string in uppercase"));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(((String) params[0]).toUpperCase());
    }
}
