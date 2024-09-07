package ktest.domain.xunit;

public record XUnitFailure(String message, String content) implements XmlUtils {
    @Override
    public String toXml() {
        if (content == null) {
            return STR."<failure message=\"\{fullClean(message)}\"/>";
        }
        return STR."<failure message=\"\{fullClean(message)}\">\{minimalClean(content)}</failure>";
    }
}

