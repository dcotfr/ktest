package fr.dcotton.ktest.domain.xunit;

record XUnitProperty(String name, String value) implements XmlUtils {
    public String toXml() {
        final var res = new StringBuilder("<property");
        res.append(" name=\"").append(cleanText(name)).append("\"");
        res.append(" value=\"").append(cleanText(value)).append("\"");
        res.append("/>");
        return res.toString();
    }
}
