package ktest.domain.xunit;

import ktest.core.XmlUtils;

public record XSystemOut(String content) implements XmlUtils {
    public String toXml() {
        return "<system-out>" + minimalClean(content) + "</system-out>";
    }
}
