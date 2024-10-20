package ktest.domain.xlsx;

import ktest.core.XmlUtils;

record Border(Style left, Style right, Style top, Style bottom) implements XmlUtils {
    final static Border NONE = new Border(Style.NONE, Style.NONE, Style.NONE, Style.NONE);

    enum Style {NONE, THIN, DOTTED}

    private static String borderDirection(final String pDirection, final Border.Style pStyle) {
        final var res = new StringBuilder();
        if (pStyle != Style.NONE) {
            res.append('<').append(pDirection).append(" style=\"").append(pStyle.name().toLowerCase()).append("\">")
                    .append("<color rgb=\"FF000000\"/>")
                    .append("</").append(pDirection).append('>');
        }
        return res.toString();
    }

    @Override
    public String toXml() {
        if (left == Style.NONE && right == Style.NONE && top == Style.NONE && bottom == Style.NONE) {
            return "<border/>";
        }

        return "<border>" +
                borderDirection("left", left) +
                borderDirection("right", right) +
                borderDirection("top", top) +
                borderDirection("bottom", bottom) +
                "</border>";
    }
}
