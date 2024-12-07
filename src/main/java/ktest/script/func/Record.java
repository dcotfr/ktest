package ktest.script.func;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Record extends Func {
    protected Record() {
        super("record", new FuncDoc(MISC, "", "{\"topic\": \"...}", "Returns a json description of the last record found."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var lastRecord = pContext.lastRecord();
        return new Txt(lastRecord != null ? lastRecord.toString() : "");
    }
}
