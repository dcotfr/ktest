package ktest.script.func.hash;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import java.util.Base64;

import static ktest.script.func.FuncType.HASH;

@ApplicationScoped
public class Decode64 extends Func {
    protected Decode64() {
        super("decode64", new FuncDoc(HASH, "\"VGV4dA==\"", "\"Text\"", "Returns the decoded text of a base64 encoding string."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        try {
            final var res = Base64.getDecoder().decode((String) params[0]);
            return new Txt(new String(res));
        } catch (final IllegalArgumentException e) {
            throw new ScriptException("Invalid base64 string in " + command() + ": " + params[0]);
        }
    }
}