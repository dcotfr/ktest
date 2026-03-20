package ktest.script.func;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Env extends Func {
    protected Env() {
        super("env", new FuncDoc(MISC, "\"SHELL\", \"default\"", "\"/bin/bash\"", "Returns the value of an ENV variable (with optional default)."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractUnboundParams(pContext, pParam, String.class);
        if (params.length == 0) {
            throw new ScriptException("At least the name of ENV variable is required.");
        } else if (params.length > 2) {
            throw new ScriptException("Too many arguments: only support name of ENV variable and optional default value.");
        }
        final var res = System.getenv((String) params[0]);
        if (res != null) {
            return new Txt(res);
        }
        return new Txt(params.length == 2 ? (String) params[1] : "");
    }
}
