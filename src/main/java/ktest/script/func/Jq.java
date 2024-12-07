package ktest.script.func;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.token.*;

import static ktest.script.func.FuncType.MISC;

@ApplicationScoped
public class Jq extends Func {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    protected Jq() {
        super("jq", new FuncDoc(MISC, "\"{\\\"a\\\":{\\\"b\\\":3.4}}\", \"/a/b\"", "3.4", "Returns the value of an attribute from a json string."));
    }

    @Override
    public Token<?> apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class, String.class);
        try {
            final var json = MAPPER.readTree((String) params[0]);
            final var jsonPointer = (String) params[1];
            final var resNode = json.at(jsonPointer.startsWith("/") ? jsonPointer : '/' + jsonPointer);
            if (resNode == null) {
                return new Txt("");
            }
            if (resNode.isNumber()) {
                return resNode.isFloatingPointNumber() ? new Flt(resNode.doubleValue()) : new Int(resNode.longValue());
            }
            return new Txt(resNode.asText());
        } catch (final JsonProcessingException e) {
            throw new ScriptException(command() + " requires a valid json string as first argument.", e);
        }
    }
}
