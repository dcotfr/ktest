package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@ApplicationScoped
public class HashSha512 extends Func {
    protected HashSha512() {
        super("hash.sha512", new FuncDoc("\"SampleString\"", "\"aee8e20df4b3ce73...e7e03c6fcda75961\"", "Returns the SHA-512 hash of the string parameter."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        try {
            final var sha512Digest = MessageDigest.getInstance("SHA-512");
            sha512Digest.update(params[0].toString().getBytes());
            return new Txt(HexFormat.of().formatHex(sha512Digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}
