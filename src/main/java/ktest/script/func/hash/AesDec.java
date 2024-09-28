package ktest.script.func.hash;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import static ktest.script.func.FuncType.HASH;

@ApplicationScoped
public class AesDec extends Func {
    protected AesDec() {
        super("aesdec", new FuncDoc(HASH, "\"B64CryptedIn\", \"B64Key\"", "\"ClearText\"", "Returns the decrypted value of base64-AES256 encrypted value."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, String.class);
        try {
            final var encrypted = Base64.getDecoder().decode((String) params[0]);
            final var iv = new byte[16];
            System.arraycopy(encrypted, 0, iv, 0, iv.length);
            final var ivParam = new IvParameterSpec(iv);

            final var key = Base64.getDecoder().decode((String) params[1]);

            final var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ivParam);
            final var cipherText = new byte[encrypted.length - 16];
            System.arraycopy(encrypted, 16, cipherText, 0, cipherText.length);

            return new Txt(new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8));
        } catch (final GeneralSecurityException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}
