package ktest.domain.xlsx;

import ktest.core.XmlUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Core implements XmlUtils {
    @Override
    public String toXml() {
        final var timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
        return XML_HEADER
                + "<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\""
                + " xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<dc:title>Test Case Matrix</dc:title>"
                + "<dc:subject/>"
                + "<dc:creator>ktest</dc:creator>"
                + "<dc:description/>"
                + "<cp:lastModifiedBy/>"
                + "<cp:revision>0</cp:revision>"
                + "<dcterms:created xsi:type=\"dcterms:W3CDTF\">" + timestamp + "</dcterms:created>"
                + "<dcterms:modified xsi:type=\"dcterms:W3CDTF\">" + timestamp + "</dcterms:modified>"
                + "</cp:coreProperties>";
    }
}
