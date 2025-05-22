package ktest.script.func;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.token.Stm;
import ktest.script.token.Token;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Coalesce extends Func {
    protected Coalesce() {
        super("coalesce", new FuncDoc(MISC, "a, \"\", 5.2", "5.2", "Returns the first defined and non empty value."));
    }

    @Override
    public Token<?> apply(final Context pContext, final Stm pParam) {
        for (var p : pParam.value()) {
            try {
                if (!"".equals(((Stm) p).evalValue(pContext))) {
                    return p;
                }
            } catch (final ScriptException e) {
                // Undefined value, continue...
            }
        }
        return new Txt("");
    }
}
