package fr.dcotton.ktest.domain.xunit;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

record XUnitProperty(@JacksonXmlProperty(isAttribute = true) String name,
                     @JacksonXmlProperty(isAttribute = true) String value) {
}
