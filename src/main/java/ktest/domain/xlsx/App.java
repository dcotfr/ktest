package ktest.domain.xlsx;

import ktest.core.XmlUtils;

public final class App implements XmlUtils {
    @Override
    public String toXml() {
        return XML_HEADER
                + "<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\"/>";
    }
}
