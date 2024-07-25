package fr.dcotton.ktest.script.func.hash;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.zip.CRC32;

import static fr.dcotton.ktest.script.func.FuncType.HASH;

@ApplicationScoped
public class Crc32 extends Func {
    protected Crc32() {
        super("crc32", new FuncDoc(HASH, "\"SampleString\"", "\"3ca8bf4\"", "Returns the CRC-32 hash of the string parameter."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        final var crc32 = new CRC32();
        crc32.update(params[0].toString().getBytes());
        return new Txt(Long.toHexString(crc32.getValue()));
    }
}
