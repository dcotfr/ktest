package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * https://github.com/testmoapp/junitxml
 */
@JacksonXmlRootElement(localName = "testsuites")
public final class XUnitReport {
    @JacksonXmlProperty(isAttribute = true)
    private int tests() {
        return testsuite.stream().mapToInt(XUnitSuite::tests).sum();
    }

    @JacksonXmlProperty(isAttribute = true)
    private int failures() {
        return testsuite.stream().mapToInt(XUnitSuite::failures).sum();
    }

    @JacksonXmlProperty(isAttribute = true)
    private int errors() {
        return testsuite.stream().mapToInt(XUnitSuite::errors).sum();
    }

    @JacksonXmlProperty(isAttribute = true)
    private int skipped() {
        return testsuite.stream().mapToInt(XUnitSuite::skipped).sum();
    }

    @JacksonXmlProperty(isAttribute = true)
    private double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    @JacksonXmlProperty(isAttribute = true)
    private final Date timestamp;

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<XUnitSuite> testsuite = new ArrayList<>();

    private final long startTimestamp;
    private long endTimestamp;

    public XUnitReport() {
        startTimestamp = System.currentTimeMillis();
        timestamp = new Date(startTimestamp);
    }

    public XUnitSuite startNewSuite(final String pName) {
        final var res = new XUnitSuite(pName);
        testsuite.add(res);
        return res;
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }
}
