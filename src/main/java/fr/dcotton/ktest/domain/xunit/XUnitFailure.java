package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record XUnitFailure(@JacksonXmlProperty(isAttribute = true) String message) {
}

