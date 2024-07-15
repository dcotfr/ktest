package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
public class Base64Encode extends Func {
    protected Base64Encode() {
        super("base64.encode", new FuncDoc("\"SampleString\"", "\"U2FtcGxlU3RyaW5n\"", "Returns the base64 encoding of a string."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(Base64.getEncoder().encodeToString(((String) params[0]).getBytes(StandardCharsets.UTF_8)));
    }
}