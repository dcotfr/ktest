package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public final class XUnitCase {
    @JacksonXmlProperty(isAttribute = true)
    private String name;

    @JacksonXmlProperty(isAttribute = true)
    private double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    @JacksonXmlElementWrapper(localName = "properties")
    private List<XUnitProperty> property = new ArrayList<>();

    @JacksonXmlProperty
    XUnitSkipped skipped;

    @JacksonXmlProperty
    XUnitFailure failure;

    @JacksonXmlProperty
    XUnitError error;

    private final long startTimestamp;
    private long endTimestamp;

    XUnitCase(final String pName) {
        startTimestamp = System.currentTimeMillis();
        name = pName;
    }

    public void addProperty(final String pKey, final String pValue) {
        property.add(new XUnitProperty(pKey, pValue));
    }

    public void fail(final String pMessage) {
        failure = new XUnitFailure(pMessage);
    }

    public void error(final String pMessage) {
        error = new XUnitError(pMessage);
    }

    public void skip(final String pMessage) {
        skipped = new XUnitSkipped(pMessage);
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }
}
