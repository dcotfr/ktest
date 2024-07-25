package fr.dcotton.ktest.script.func.text;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static fr.dcotton.ktest.script.func.FuncType.TEXT;

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
