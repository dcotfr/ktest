package ktest.domain.xunit;

public record XUnitError(String message, Throwable throwable) implements XmlUtils {
    @Override
    public String toXml() {
        if (throwable == null) {
            return "<error message=\"" + fullClean(message) + "\"/>";
        }
        final var res = new StringBuilder("<error message=\"" + fullClean(message) + "\">");
        for (final var l : throwable.getStackTrace()) {
            res.append(minimalClean(l.toString())).append('\n');
        }
        res.append("</error>");
        return res.toString();
    }
}
