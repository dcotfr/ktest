package ktest.core;

import ktest.domain.xunit.XUnitProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlUtilsTest {
    @Test
    void minimalCleanTest() {
        final var p = new XUnitProperty("k", "v");
        assertEquals("&amp;lt;", p.minimalClean("&lt;"));
    }

    @Test
    void fullCleanTest() {
        final var p = new XUnitProperty("k", "v");
        assertEquals("&amp;&lt;&quot;&apos;&gt;", p.fullClean("&<\"'>"));
    }
}
