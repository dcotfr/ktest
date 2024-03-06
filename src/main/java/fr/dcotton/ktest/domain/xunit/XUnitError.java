package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record XUnitError(@JacksonXmlProperty(isAttribute = true) String message) {
}
