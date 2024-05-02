package fr.dcotton.ktest.domain.xunit;

public record XUnitSkipped(String message) implements XmlUtils {
    @Override
    public String toXml() {
        return "<skipped message=\"" + fullClean(message) + "\"/>";
    }
}
