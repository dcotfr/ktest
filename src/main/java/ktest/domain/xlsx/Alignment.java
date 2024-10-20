package ktest.domain.xlsx;

import ktest.core.XmlUtils;

record Alignment(boolean hCenter, boolean rotation) implements XmlUtils {
    final static Alignment CENTER = new Alignment(true, false);
    final static Alignment LEFT = new Alignment(false, false);
    final static Alignment ROTATED = new Alignment(true, true);

    @Override
    public String toXml() {
        final var res = new StringBuilder("<alignment vertical=\"center\"");
        if (hCenter) {
            res.append(" horizontal=\"center\"");
        }
        if (rotation) {
            res.append(" textRotation=\"255\"");
        }
        return res.append("/>").toString();
    }
}
