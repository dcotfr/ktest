package ktest.domain.xunit;

public record XSystemOut(String content) implements XmlUtils {
    public String toXml() {
        return STR."<system-out>\{minimalClean(content)}</system-out>";
    }
}
