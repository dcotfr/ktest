package ktest.core;

public interface XmlUtils {
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
