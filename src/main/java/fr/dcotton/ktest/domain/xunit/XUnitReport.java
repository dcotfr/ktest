package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/testmoapp/junitxml
 */
public final class XUnitReport implements XmlUtils {
    public int tests() {
        return testsuite.stream().mapToInt(XUnitSuite::tests).sum();
    }

    public int failures() {
        return testsuite.stream().mapToInt(XUnitSuite::failures).sum();
    }

    public int errors() {
        return testsuite.stream().mapToInt(XUnitSuite::errors).sum();
    }

    public int skipped() {
        return testsuite.stream().mapToInt(XUnitSuite::skipped).sum();
    }

    public int assertions() {
        return testsuite.stream().mapToInt(XUnitSuite::assertions).sum();
    }

    public double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    @JsonIgnore
    public final OffsetDateTime timestamp;

    public final List<XUnitSuite> testsuite = new ArrayList<>();

    private final long startTimestamp;
    private long endTimestamp;

    public XUnitReport() {
        startTimestamp = System.currentTimeMillis();
        timestamp = OffsetDateTime.now();
    }

    public XUnitSuite startNewSuite(final String pName) {
        final var res = new XUnitSuite(pName);
        testsuite.add(res);
        return res;
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }

    public String toXml() {
        final var res = new StringBuilder("<testsuites");
        res.append(" timestamp=\"").append(timestamp).append("\"");
        res.append(" time=\"").append(time()).append("\"");
        res.append(" errors=\"").append(errors()).append("\"");
        res.append(" skipped=\"").append(skipped()).append("\"");
        res.append(" tests=\"").append(tests()).append("\"");
        res.append(" failures=\"").append(failures()).append("\"");
        res.append(" assertions=\"").append(assertions()).append("\"");
        res.append('>');
        testsuite.stream().map(XUnitSuite::toXml).forEach(res::append);
        res.append("</testsuites>");
        return res.toString();
    }
}
