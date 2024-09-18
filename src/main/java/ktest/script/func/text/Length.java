package ktest.script.func.text;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class Length extends Func {
    protected Length() {
        super("length", new FuncDoc(TEXT, "\"Short text\"", "10", "Returns the length of a string."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Int(((String) params[0]).length());
    }
}
