package fr.dcotton.ktest.domain.xunit;

public record XUnitSkipped(String message) implements XmlUtils {
    @Override
    public String toXml() {
        final var res = new StringBuilder("<skipped");
        res.append(" message=\"").append(cleanText(message)).append("\"");
        res.append("/>");
        return res.toString();
    }
}
