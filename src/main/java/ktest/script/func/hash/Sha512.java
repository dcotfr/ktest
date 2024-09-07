package ktest.script.func.hash;

import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static ktest.script.func.FuncType.HASH;

@ApplicationScoped
public class Sha512 extends Func {
    protected Sha512() {
        super("sha512", new FuncDoc(HASH, "\"SampleString\"", "\"aee8e20df4b3ce73...e7e03c6fcda75961\"", "Returns the SHA-512 hash of the string parameter."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        try {
            final var sha512Digest = MessageDigest.getInstance("SHA-512");
            sha512Digest.update(params[0].toString().getBytes());
            return new Txt(HexFormat.of().formatHex(sha512Digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new ScriptException(STR."\{command()} interrupted.", e);
        }
    }
}
