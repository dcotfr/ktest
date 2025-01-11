package ktest.domain.xunit;

import ktest.core.XmlUtils;

import java.util.ArrayList;
import java.util.List;

public final class XUnitCase implements XmlUtils {
    public final String name;

    public final String classname;

    public final boolean assertion;

    public double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    public final List<XUnitProperty> properties = new ArrayList<>();

    public XUnitSkipped skipped;

    public XUnitFailure failure;

    public XUnitError error;

    private final long startTimestamp;
    private long endTimestamp;

    XUnitCase(final String pName, final String pClassName, final boolean pAssertion) {
        startTimestamp = System.currentTimeMillis();
        name = pName;
        classname = pClassName;
        assertion = pAssertion;
    }

    public void addProperty(final String pKey, final String pValue) {
        properties.add(new XUnitProperty(pKey, pValue));
    }

    public void fail(final String pMessage, final String pContent) {
        failure = new XUnitFailure(pMessage, pContent);
    }

    public void error(final String pMessage, final Throwable pThrowable) {
        error = new XUnitError(pMessage, pThrowable);
    }

    public void skip(final String pMessage) {
        skipped = new XUnitSkipped(pMessage);
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }

    public String toXml() {
        final var res = new StringBuilder("<testcase");
        res.append(" name=\"").append(fullClean(name)).append("\"");
        if (classname != null) {
            res.append(" classname=\"").append(fullClean(classname)).append("\"");
        }
        res.append(" time=\"").append(time()).append("\"");
        res.append('>');
        if (!properties.isEmpty()) {
            res.append("<properties>");
            properties.stream().map(XUnitProperty::toXml).forEach(res::append);
            res.append("</properties>");
        }
        if (skipped != null) {
            res.append(skipped.toXml());
        }
        if (failure != null) {
            res.append(failure.toXml());
        }
        if (error != null) {
            res.append(error.toXml());
        }
        res.append("</testcase>");
        return res.toString();
    }
}
