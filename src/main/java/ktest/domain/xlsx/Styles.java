package ktest.domain.xlsx;

import ktest.core.XmlUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static ktest.domain.xlsx.Alignment.CENTER;
import static ktest.domain.xlsx.Border.NONE;
import static ktest.domain.xlsx.Font.PLAIN;

final class Styles implements XmlUtils {
    private final Map<Font, Integer> fonts = new HashMap<>();
    private final Map<Border, Integer> borders = new HashMap<>();
    private final Map<Xf, Integer> cellXfs = new HashMap<>();

    Styles() {
        fonts.put(PLAIN, 0);
        borders.put(NONE, 0);
    }

    private int borderId(final Border pBorder) {
        final var border = pBorder != null ? pBorder : NONE;
        return borders.computeIfAbsent(border, _ -> borders.size());
    }

    private int fontId(final Font pFont) {
        final var font = pFont != null ? pFont : PLAIN;
        return fonts.computeIfAbsent(font, _ -> fonts.size());
    }

    @Override
    public String toXml() {
        final var res = new StringBuilder(XML_HEADER)
                .append("<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");

        res.append("<fonts count=\"").append(fonts.size()).append("\">");
        fonts.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .forEach(f -> res.append(f.toXml()));
        res.append("</fonts>");

        res.append("<borders count=\"").append(borders.size()).append("\">");
        borders.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .forEach(b -> res.append(b.toXml()));
        res.append("</borders>");

        res.append("<cellXfs count=\"").append(cellXfs.size()).append("\">");
        cellXfs.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .forEach(xf -> res.append(xf.toXml()));
        res.append("</cellXfs>");

        return res.append("</styleSheet>").toString();
    }

    int xfId(final Font pFont, final Border pBorder, final Alignment pAlignment) {
        final var alignment = pAlignment != null ? pAlignment : CENTER;
        return cellXfs.computeIfAbsent(new Xf(fontId(pFont), borderId(pBorder), alignment), _ -> cellXfs.size());
    }
}
