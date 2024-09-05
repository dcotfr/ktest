package ktest.domain.xunit;

interface XmlUtils {
    String toXml();

    default String minimalClean(final String pString) {
        return pString == null ? "" :
                pString.replaceAll("<", "&lt;")
                        .replaceAll("&", "&amp;");
    }

    default String fullClean(final String pString) {
        return pString == null ? "" :
                minimalClean(pString)
                        .replaceAll("\"", "&quot;")
                        .replaceAll("'", "&apos;")
                        .replaceAll(">", "&gt;");
    }
}
