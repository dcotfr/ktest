package ktest.core;

public interface XmlUtils {
    String toXml();

    default String minimalClean(final String pString) {
        return pString == null ? "" :
                pString.replace("&", "&amp;")
                        .replace("<", "&lt;");
    }

    default String fullClean(final String pString) {
        return pString == null ? "" :
                minimalClean(pString)
                        .replace("\"", "&quot;")
                        .replace("'", "&apos;")
                        .replace(">", "&gt;");
    }
}
