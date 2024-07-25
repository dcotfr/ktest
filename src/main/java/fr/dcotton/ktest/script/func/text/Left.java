package fr.dcotton.ktest.script.func.text;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static fr.dcotton.ktest.script.func.FuncType.TEXT;

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
