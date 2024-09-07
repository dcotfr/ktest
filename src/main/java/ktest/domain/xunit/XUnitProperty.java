package ktest.domain.xunit;

public record XUnitProperty(String name, String value) implements XmlUtils {
    public String toXml() {
        return STR."<property name=\"\{fullClean(name)}\" value=\"\{fullClean(value)}\"/>";
    }
}
