package fr.dcotton.ktest.script.func.text;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LTrim extends Func {
    protected LTrim() {
        super("ltrim", new FuncDoc("\" Test \"", "\"Test \"", "Returns the with all left spaces removed."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(((String) params[0]).stripLeading());
    }
}

