package fr.dcotton.ktest.domain.xunit;

interface XmlUtils {
    String toXml();

    default String cleanText(final String pString) {
        return pString != null ? pString.replaceAll("\"", "&quot;") : "";
    }
}
