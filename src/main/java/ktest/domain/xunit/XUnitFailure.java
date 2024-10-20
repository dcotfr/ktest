package ktest.domain.xunit;

import ktest.core.XmlUtils;

public record XUnitFailure(String message, String content) implements XmlUtils {
    @Override
    public String toXml() {
        if (content == null) {
            return "<failure message=\"" + fullClean(message) + "\"/>";
        }
        return "<failure message=\"" + fullClean(message) + "\">" + minimalClean(content) + "</failure>";
    }
}

