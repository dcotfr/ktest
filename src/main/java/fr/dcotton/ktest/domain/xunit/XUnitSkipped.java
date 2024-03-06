package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record XUnitSkipped(@JacksonXmlProperty(isAttribute = true) String message) {
}
