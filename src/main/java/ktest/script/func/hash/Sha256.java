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
public class Sha256 extends Func {
    protected Sha256() {
        super("sha256", new FuncDoc(HASH, "\"SampleString\"", "\"77b12c9c6213a05f...8b2c34769ec4fc20\"", "Returns the SHA-256 hash of the string parameter."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        try {
            final var sha256Digest = MessageDigest.getInstance("SHA-256");
            sha256Digest.update(params[0].toString().getBytes());
            return new Txt(HexFormat.of().formatHex(sha256Digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new ScriptException(STR."\{command()} interrupted.", e);
        }
    }
}
