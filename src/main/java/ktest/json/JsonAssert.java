package ktest.json;

import ktest.core.KTestException;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.ArrayList;
import java.util.List;

public final class JsonAssert {
    private JsonAssert() {
    }

    public static List<Failure> contains(final String pExpected, final String pActual) {
        final var res = new ArrayList<Failure>();
        if (pExpected == null || pExpected.isEmpty()) {
            return res;
        }
        try {
            final var comp = JSONCompare.compareJSON(forceJson(pExpected), forceJson(pActual), JSONCompareMode.LENIENT);
            if (comp.failed()) {
                final var cols = comp.getMessage().split("\\[\\]: ");
                if (cols.length == 2) {
                    final var message = cols[1].toLowerCase().replace(" 0 values ", " 0 value ").replace(" 1 values ", " 1 value ");
                    res.add(new BadArraySize(cols[0], message));
                }

                comp.getFieldFailures().forEach(f -> res.add(new ContentMismatch(f.getField(),
                        f.getExpected() != null ? f.getExpected().toString() : null,
                        f.getActual() != null ? f.getActual().toString() : null)));
                comp.getFieldMissing().forEach(f -> res.add(new MissingField(f.getExpected() != null ? f.getExpected().toString() : null)));
            }
            return res;
        } catch (final JSONException e) {
            throw new KTestException("Invalid JSON.", e);
        }
    }

    private static String forceJson(final String pValue) {
        if (pValue.length() < 2) {
            return "\"" + pValue + "\"";
        }
        final var startChar = pValue.charAt(0);
        final var endChar = pValue.charAt(pValue.length() - 1);
        if ((startChar == '{' && endChar == '}') || (startChar == '[' && endChar == ']') || (startChar == '\"' && endChar == '\"')) {
            return pValue;
        }
        return "\"" + pValue + "\"";
    }
}
