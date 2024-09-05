package ktest.script.func.hash;

import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.zip.CRC32;

import static ktest.script.func.FuncType.HASH;

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
