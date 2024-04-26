package fr.dcotton.ktest.domain.xunit;

public record XUnitProperty(String name, String value) implements XmlUtils {
    public String toXml() {
        return "<property name=\"" + cleanText(name) + "\" value=\"" + cleanText(value) + "\"/>";
    }
}
