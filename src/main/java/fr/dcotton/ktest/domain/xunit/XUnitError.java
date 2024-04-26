package fr.dcotton.ktest.domain.xunit;

public record XUnitError(String message) implements XmlUtils {
    @Override
    public String toXml() {
        return "<error message=\"" + cleanText(message) + "\"/>";
    }
}
