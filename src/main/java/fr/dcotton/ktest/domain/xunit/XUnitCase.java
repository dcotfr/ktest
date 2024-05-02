package fr.dcotton.ktest.domain.xunit;

import java.util.ArrayList;
import java.util.List;

public final class XUnitCase implements XmlUtils {
    public final String name;

    public final boolean assertion;

    public String classname;

    public double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    public final List<XUnitProperty> properties = new ArrayList<>();

    public XUnitSkipped skipped;

    public XUnitFailure failure;

    public XUnitError error;

    public final List<XSystemOut> systemOuts = new ArrayList<>();

    private final long startTimestamp;
    private long endTimestamp;

    XUnitCase(final String pName, final boolean pAssertion) {
        startTimestamp = System.currentTimeMillis();
        name = pName;
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

    public void className(final String pClassName) {
        classname = pClassName;
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }

    public void addSystemOut(final String pContent) {
        systemOuts.add(new XSystemOut(pContent));
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
        systemOuts.stream().map(XSystemOut::toXml).forEach(res::append);
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
