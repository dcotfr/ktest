package fr.dcotton.ktest.domain.xunit;

import fr.dcotton.ktest.domain.Action;

import java.util.ArrayList;
import java.util.List;

public final class XUnitCase implements XmlUtils {
    public final String name;

    public String classname;

    public double time() {
        return ((endTimestamp != 0L ? endTimestamp : System.currentTimeMillis()) - startTimestamp) / 1000.0;
    }

    public final List<XUnitProperty> properties = new ArrayList<>();

    public XUnitSkipped skipped;

    public XUnitFailure failure;

    public XUnitError error;

    private final long startTimestamp;
    private long endTimestamp;

    XUnitCase(final String pName) {
        startTimestamp = System.currentTimeMillis();
        name = pName;
    }

    public void addProperty(final String pKey, final String pValue) {
        properties.add(new XUnitProperty(pKey, pValue));
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

    public void details(final Action pAction, final String pBroker, final String pTopic) {
        classname = pAction.name() + '(' + pTopic + "@" + pBroker + ')';
    }

    public void end() {
        endTimestamp = System.currentTimeMillis();
    }

    public String toXml() {
        final var res = new StringBuilder("  <testcase");
        res.append(" name=\"").append(cleanText(name)).append("\"");
        res.append(" classname=\"").append(cleanText(classname)).append("\"");
        res.append(" time=\"").append(time()).append("\"");
        res.append(">\n");
        res.append("   <properties>\n");
        properties.stream().map(XUnitProperty::toXml).forEach(res::append);
        res.append("   </properties>\n");
        res.append("  </testcase>\n");
        return res.toString();
    }
}
