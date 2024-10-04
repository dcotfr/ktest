package ktest.script.func.hex;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Int;
import ktest.script.token.Stm;

import java.math.BigInteger;

import static ktest.script.func.FuncType.HEX;

@ApplicationScoped
public class Hex2Int extends Func {
    protected Hex2Int() {
        super("hex2int", new FuncDoc(HEX, "\"7fff\"", "32767", "Returns the integer value of an hexadecimal string."));
    }

    @Override
    public Int apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        final var hexStr = params[0].toString();
        try {
            return new Int(new BigInteger(hexStr, 16).longValue());
        } catch (final NumberFormatException e) {
            throw new ScriptException("A valid hexadecimal string is expected in " + command() + ": " + hexStr + " found.");
        }
    }
}
