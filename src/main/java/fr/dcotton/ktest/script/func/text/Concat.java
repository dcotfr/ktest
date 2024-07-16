package fr.dcotton.ktest.script.func.text;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Concat extends Func {
    protected Concat() {
        super("concat", new FuncDoc("\"Aaa\", \"Bbb\"", "\"AaaBbb\"", "Returns the concatenation of 2 strings."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, String.class);
        return new Txt(params[0] + ((String) params[1]));
    }
}
