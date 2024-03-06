package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

abstract class XUnitResult {
    @JacksonXmlProperty(isAttribute = true)
    private String message;
}
