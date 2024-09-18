package ktest.script.func.text;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class Replace extends Func {
    protected Replace() {
        super("replace", new FuncDoc(TEXT, "\"ABAB\", \"B\", \"a\"", "\"AaAa\"", "Returns a new string with old substring replaced by new substring."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, String.class, String.class);
        return new Txt(((String) params[0]).replace((String) params[1], (String) params[2]));
    }
}
