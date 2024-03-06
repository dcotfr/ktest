package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XUnitSuite {
    @JacksonXmlProperty(isAttribute = true)
    private final String name;

    @JacksonXmlProperty(isAttribute = true)
    int tests() {
        return testcase.size();
    }

    @JacksonXmlProperty(isAttribute = true)
    int failures() {
        return (int) testcase.stream().filter(tc -> tc.failure != null).count();
    }

    @JacksonXmlProperty(isAttribute = true)
    int errors() {
        return (int) testcase.stream().filter(tc -> tc.error != null).count();
    }

    @JacksonXmlProperty(isAttribute = true)
    int skipped() {
        return (int) testcase.stream().filter(tc -> tc.skipped != null).count();
    }

    @JacksonXmlProperty(isAttribute = true)
    private double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    @JacksonXmlProperty(isAttribute = true)
    private final Date timestamp;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<XUnitCase> testcase = new ArrayList<>();

    private final long startTimestamp;
    private long endTimestamp;

    XUnitSuite(final String pName) {
        startTimestamp = System.currentTimeMillis();
        timestamp = new Date(startTimestamp);
        name = pName;
    }

    public XUnitCase startNewCase(final String pName) {
        final var res = new XUnitCase(pName);
        testcase.add(res);
        return res;
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }
}
