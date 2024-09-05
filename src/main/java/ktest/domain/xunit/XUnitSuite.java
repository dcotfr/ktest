package ktest.domain.xunit;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class XUnitSuite implements XmlUtils {
    public final String name;

    public int tests() {
        return testcase.size();
    }

    public int failures() {
        return (int) testcase.stream().filter(tc -> tc.failure != null).count();
    }

    public int errors() {
        return (int) testcase.stream().filter(tc -> tc.error != null).count();
    }

    public int skipped() {
        return (int) testcase.stream().filter(tc -> tc.skipped != null).count();
    }

    public int assertions() {
        return (int) testcase.stream().filter(tc -> tc.assertion).count();
    }

    public double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    public final OffsetDateTime timestamp;

    public final List<XUnitCase> testcase = new ArrayList<>();

    private final long startTimestamp;
    private long endTimestamp;

    XUnitSuite(final String pName) {
        startTimestamp = System.currentTimeMillis();
        timestamp = OffsetDateTime.now();
        name = pName;
    }

    public XUnitCase startNewCase(final String pName, final String pClassName, final boolean pAssertion) {
        final var res = new XUnitCase(pName, pClassName,pAssertion);
        testcase.add(res);
        return res;
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }

    public String toXml() {
        final var res = new StringBuilder("<testsuite");
        res.append(" name=\"").append(fullClean(name)).append("\"");
        res.append(" timestamp=\"").append(timestamp).append("\"");
        res.append(" time=\"").append(time()).append("\"");
        res.append(" errors=\"").append(errors()).append("\"");
        res.append(" skipped=\"").append(skipped()).append("\"");
        res.append(" tests=\"").append(tests()).append("\"");
        res.append(" failures=\"").append(failures()).append("\"");
        res.append(" assertions=\"").append(assertions()).append("\"");
        res.append('>');
        testcase.stream().map(XUnitCase::toXml).forEach(res::append);
        res.append("</testsuite>");
        return res.toString();
    }
}
