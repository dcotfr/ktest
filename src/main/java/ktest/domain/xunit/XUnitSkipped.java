package ktest.domain.xunit;

import ktest.core.XmlUtils;

public record XUnitSkipped(String message) implements XmlUtils {
    @Override
    public String toXml() {
        return "<skipped message=\"" + fullClean(message) + "\"/>";
    }
}
