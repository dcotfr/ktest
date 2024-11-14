package ktest.domain.xlsx;

import ktest.core.XmlUtils;

record Font(String name, String color, int size, boolean bold) implements XmlUtils {
    final static Font PLAIN = new Font("Arial", "FF000000", 10, false);
    final static Font BOLD = new Font("Arial", "FF000000", 10, true);
    final static Font SMALL = new Font("Arial", "FF000000", 8, false);
    final static Font BIG = new Font("Arial", "FF000000", 12, true);

    @Override
    public String toXml() {
        final var res = new StringBuilder("<font>")
                .append("<name val=\"").append(name).append("\"/>")
                .append("<color rgb=\"").append(color).append("\"/>")
                .append("<sz val=\"").append(size).append("\"/>");
        if (bold) {
            res.append("<b val=\"1\"/>");
        }
        return res.append("</font>").toString();
    }
}
