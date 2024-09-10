package ktest.script.func;

import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.token.Stm;
import ktest.script.token.Token;

import java.util.function.BiFunction;

/**
 * https://docs.gomplate.ca/functions/
 * base64.encode(String) -> String
 * base64.decode(String) -> String
 */
public abstract class Func implements BiFunction<Context, Stm, Token<?>> {
    private final String command;
    private final FuncDoc doc;

    protected Func(final String pCommand, final FuncDoc pDoc) {
        command = pCommand;
        doc = pDoc;
    }

    public final String command() {
        return command;
    }

    public final FuncDoc doc() {
        return doc;
    }

    protected final Object[] extractParam(final Context pContext, final Stm pParam, final Class<?>... pTypes) {
        final var expected = pTypes.length;
        final var found = pParam.value().size();
        if (expected != found) {
            throw new ScriptException(STR."Invalid number of arguments in \{command()}: \{expected} expected, \{found} found.");
        }
        final var res = new Object[expected];
        for (int i = 0; i < expected; i++) {
            final var v = ((Stm) pParam.value().get(i)).evalValue(pContext);
            if (!pTypes[i].isAssignableFrom(v.getClass())) {
                throw new ScriptException(STR."Invalid type of argument in \{command()}: \{pTypes[i]} expected, \{v.getClass()} found.");
            }
            res[i] = v;
        }
        return res;
    }

    protected final Object[] extractUnboundParams(final Context pContext, final Stm pParam, final Class<?> pType) {
        final var expected = pParam.value().size();
        final var res = new Object[expected];
        for (int i = 0; i < expected; i++) {
            final var v = ((Stm) pParam.value().get(i)).evalValue(pContext);
            if (!pType.isAssignableFrom(v.getClass())) {
                throw new ScriptException(STR."Invalid type of argument in \{command()}: \{pType} expected, \{v.getClass()} found.");
            }
            res[i] = v;
        }
        return res;
    }
}