package ktest.domain.xlsx;

import ktest.core.XmlUtils;

record Alignment(boolean vCenter, boolean hCenter, boolean rotation) implements XmlUtils {
    final static Alignment CENTER = new Alignment(true, true, false);
    final static Alignment LEFT = new Alignment(true, false, false);
    final static Alignment ROTATED = new Alignment(false, true, true);

    @Override
    public String toXml() {
        final var res = new StringBuilder("<alignment");
        if (vCenter) {
            res.append(" vertical=\"center\"");
        } else {
            res.append(" vertical=\"top\"");
        }
        if (hCenter) {
            res.append(" horizontal=\"center\"");
        }
        if (rotation) {
            res.append(" textRotation=\"255\"");
        }
        return res.append("/>").toString();
    }
}
