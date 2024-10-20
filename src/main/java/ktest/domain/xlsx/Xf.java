package ktest.domain.xlsx;

import ktest.core.XmlUtils;

record Xf(int fontId, int borderId, Alignment alignment) implements XmlUtils {
    @Override
    public String toXml() {
        return "<xf fontId=\"" + fontId + "\" borderId=\"" + borderId + "\">" + alignment.toXml() + "</xf>";
    }
}
