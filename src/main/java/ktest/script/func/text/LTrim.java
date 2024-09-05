package ktest.script.func.text;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.TEXT;

@ApplicationScoped
public class LTrim extends Func {
    protected LTrim() {
        super("ltrim", new FuncDoc(TEXT, "\" Test \"", "\"Test \"", "Returns the with all left spaces removed."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(((String) params[0]).stripLeading());
    }
}

