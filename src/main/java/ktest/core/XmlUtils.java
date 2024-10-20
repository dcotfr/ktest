package ktest.core;

public interface XmlUtils {
    String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

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
