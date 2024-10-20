package ktest.domain.xunit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ktest.core.XmlUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return ((endTimestamp > 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
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

    public XUnitReport(final List<XUnitReport> pReports) {
        var minStartTimestamp = Long.MAX_VALUE;
        var maxEndTimestamp = Long.MIN_VALUE;
        var minTimestamp = OffsetDateTime.MAX;
        for (final var report : pReports) {
            if (report.startTimestamp < minStartTimestamp) {
                minStartTimestamp = report.startTimestamp;
            }
            if (report.endTimestamp > maxEndTimestamp) {
                maxEndTimestamp = report.endTimestamp;
            }
            if (report.timestamp.isBefore(minTimestamp)) {
                minTimestamp = report.timestamp;
            }
            testsuite.addAll(report.testsuite);
        }
        startTimestamp = minStartTimestamp;
        endTimestamp = maxEndTimestamp;
        timestamp = minTimestamp;
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
