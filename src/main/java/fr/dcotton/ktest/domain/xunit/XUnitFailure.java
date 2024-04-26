package fr.dcotton.ktest.domain.xunit;

public record XUnitFailure(String message) implements XmlUtils {
    @Override
    public String toXml() {
        return "<failure message=\"" + cleanText(message) + "\"/>";
    }
}

