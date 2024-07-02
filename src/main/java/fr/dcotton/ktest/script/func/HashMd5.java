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
public class HashMd5 extends Func {
    protected HashMd5() {
        super("hash.md5", new FuncDoc("\"SampleString\"", "\"ec1dd92925cb06934c047fb3f5380cba\"", "Returns the MD5 hash of the string parameter."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        try {
            final var md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(params[0].toString().getBytes());
            return new Txt(HexFormat.of().formatHex(md5Digest.digest()));
        } catch (final NoSuchAlgorithmException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}

