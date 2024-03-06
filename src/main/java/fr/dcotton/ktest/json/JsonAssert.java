package fr.dcotton.ktest.json;

import fr.dcotton.ktest.core.KTestException;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.ArrayList;
import java.util.List;

public final class JsonAssert {
    private JsonAssert() {
    }

    public static List<Failure> contains(final String pExpected, final String pActual) {
        try {
            final var res = new ArrayList<Failure>();
            final var comp = JSONCompare.compareJSON(pExpected, pActual, JSONCompareMode.STRICT_ORDER);
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
}
