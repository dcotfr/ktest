package ktest.script.func.hash;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static ktest.script.func.FuncType.HASH;

@ApplicationScoped
public class AesKey extends Func {
    protected AesKey() {
        super("aeskey", new FuncDoc(HASH, "", "\"ygrS4...ijP8=\"", "Returns a new random base-64-encoded AES256 key."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        extractParam(pContext, pParam);
        try {
            final var aes256KeyGen = KeyGenerator.getInstance("AES");
            aes256KeyGen.init(256);
            return new Txt(Base64.getEncoder().encodeToString(aes256KeyGen.generateKey().getEncoded()));
        } catch (final NoSuchAlgorithmException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}
