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
public class Md5 extends Func {
    protected Md5() {
        super("md5", new FuncDoc(HASH, "\"SampleString\"", "\"ec1dd92925cb06934c047fb3f5380cba\"", "Returns the MD5 hash of the string parameter."));
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

