package ktest.script.func.crypt;

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
import java.security.SecureRandom;
import java.util.Base64;

import static ktest.script.func.FuncType.CRYPT;

@ApplicationScoped
public class AesEnc extends Func {
    protected AesEnc() {
        super("aesenc", new FuncDoc(CRYPT, "\"ClearText\", \"B64Key\"", "\"B64CryptedOut\"", "Returns the base64 form of the value encrypted with AES256."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, String.class);
        try {
            final var key = Base64.getDecoder().decode((String) params[1]);

            final var rnd = new SecureRandom();
            final var iv = new byte[16];
            rnd.nextBytes(iv);
            final var ivParam = new IvParameterSpec(iv);

            final var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), ivParam);
            final var cipherText = cipher.doFinal(((String) params[0]).getBytes(StandardCharsets.UTF_8));
            final var encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
            return new Txt(Base64.getEncoder().encodeToString(encryptedData));
        } catch (final GeneralSecurityException e) {
            throw new ScriptException(command() + " interrupted.", e);
        }
    }
}
