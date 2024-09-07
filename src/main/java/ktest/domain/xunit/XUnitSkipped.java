package ktest.domain.xunit;

public record XUnitSkipped(String message) implements XmlUtils {
    @Override
    public String toXml() {
        return STR."<skipped message=\"\{fullClean(message)}\"/>";
    }
}
