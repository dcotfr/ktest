package fr.dcotton.ktest.script.func.hash;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Base64;

import static fr.dcotton.ktest.script.func.FuncType.HASH;

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