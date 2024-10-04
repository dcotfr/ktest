package ktest.script.func.hex;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;

import static ktest.script.func.FuncType.HEX;

@ApplicationScoped
public class Int2Hex extends Func {
    protected Int2Hex() {
        super("int2hex", new FuncDoc(HEX, "32767", "\"7fff\"", "Returns the hexadecimal representation of number."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, Number.class);
        return new Txt(Long.toHexString(((Number) params[0]).longValue()));
    }
}
