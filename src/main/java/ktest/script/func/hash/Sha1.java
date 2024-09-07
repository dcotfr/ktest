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
public class Sha1 extends Func {
    protected Sha1() {
        super("sha1", new FuncDoc(HASH, "\"SampleString\"", "\"ac7fc7261c573830...f20bf0d74d1443cd\"", "Returns the SHA-1 hash of the string parameter."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        try {
            final var sha1Digest = MessageDigest.getInstance("SHA-1");
            sha1Digest.update(params[0].toString().getBytes());
            return new Txt(HexFormat.of().formatHex(sha1Digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new ScriptException(STR."\{command()} interrupted.", e);
        }
    }
}
