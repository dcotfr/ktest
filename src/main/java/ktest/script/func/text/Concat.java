package ktest.script.func.text;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class Concat extends Func {
    protected Concat() {
        super("concat", new FuncDoc(TEXT, "\"Aaa\", \"Bbb\",...", "\"AaaBbb\"", "Returns the concatenation of multiple strings."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractUnboundParams(pContext, pParam, String.class);
        final var res = new StringBuilder();
        for (final var s : params) {
            res.append((String) s);
        }
        return new Txt(res.toString());
    }
}
