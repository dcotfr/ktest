package ktest.domain.xunit;

import ktest.core.XmlUtils;

public record XUnitProperty(String name, String value) implements XmlUtils {
    public String toXml() {
        return "<property name=\"" + fullClean(name) + "\" value=\"" + fullClean(value) + "\"/>";
    }
}
