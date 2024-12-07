package ktest.core;

public interface XmlUtils {
    String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    String toXml();

    default String minimalClean(final String pString) {
        return pString == null ? "" :
                pString.replace("<", "&lt;")
                        .replace("&", "&amp;");
    }

    default String fullClean(final String pString) {
        return pString == null ? "" :
                minimalClean(pString)
                        .replace("\"", "&quot;")
                        .replace("'", "&apos;")
                        .replace(">", "&gt;");
    }
}
