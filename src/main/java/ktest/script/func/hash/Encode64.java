package ktest.script.func.hash;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static ktest.script.func.FuncType.HASH;

@ApplicationScoped
public class Encode64 extends Func {
    protected Encode64() {
        super("encode64", new FuncDoc(HASH, "\"SampleString\"", "\"U2FtcGxlU3RyaW5n\"", "Returns the base64 encoding of a string."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(Base64.getEncoder().encodeToString(((String) params[0]).getBytes(StandardCharsets.UTF_8)));
    }
}