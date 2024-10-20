package ktest.domain.xlsx;

import ktest.core.XmlUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

final class SharedStrings implements XmlUtils {
    private final Map<String, SharedString> indexedStrings = new HashMap<>();
    private int count;

    SharedString cache(final String pString) {
        count++;
        return indexedStrings.computeIfAbsent(pString, v -> new SharedString(pString, indexedStrings.size()));
    }

    public String toXml() {
        final var res = new StringBuilder(XML_HEADER);
        res.append("<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" count=\"").append(count).append("\" uniqueCount=\"").append(indexedStrings.size()).append("\">");
        indexedStrings.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().index()))
                .forEach(e -> res.append("<si><t>").append(fullClean(e.getKey())).append("</t></si>"));
        return res.append("</sst>").toString();
    }
}
