package ktest.domain.xunit;

public record XSystemOut(String content) implements XmlUtils {
    public String toXml() {
        return "<system-out>" + minimalClean(content) + "</system-out>";
    }
}
