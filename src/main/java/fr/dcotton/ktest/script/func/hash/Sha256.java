package fr.dcotton.ktest.script.func.hash;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@ApplicationScoped
public class Sha256 extends Func {
    protected Sha256() {
        super("sha256", new FuncDoc("\"SampleString\"", "\"77b12c9c6213a05f...8b2c34769ec4fc20\"", "Returns the SHA-256 hash of the string parameter."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        try {
            final var sha256Digest = MessageDigest.getInstance("SHA-256");
            sha256Digest.update(params[0].toString().getBytes());
            return new Txt(HexFormat.of().formatHex(sha256Digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}
