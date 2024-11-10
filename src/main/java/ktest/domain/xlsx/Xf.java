package ktest.domain.xlsx;

import ktest.core.XmlUtils;

record Xf(int fontId, int borderId, Alignment alignment) implements XmlUtils {
    @Override
    public String toXml() {
        return "<xf fontId=\"" + fontId + "\" borderId=\"" + borderId + "\" applyFont=\"true\" applyBorder=\"true\" applyAlignment=\"true\">"
                + alignment.toXml() + "</xf>";
    }
}
