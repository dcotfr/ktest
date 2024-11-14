package ktest.domain.xlsx;

import ktest.core.XmlUtils;

record Xf(int fontId, int borderId, Alignment alignment) implements XmlUtils {
    @Override
    public String toXml() {
        return "<xf numFmtId=\"0\" xfId=\"0\" fontId=\"" + fontId + "\" borderId=\"" + borderId + "\" applyFont=\"1\" applyBorder=\"1\" applyAlignment=\"1\">"
                + alignment.toXml() + "</xf>";
    }
}
