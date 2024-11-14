package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CellTest {
    @Test
    void testRef() {
        assertEquals("A1", new Cell(0, 0, 0).ref());
        assertEquals("Z2", new Cell(25, 1, 0).ref());
        assertEquals("AA3", new Cell(26, 2, 0).ref());
        assertEquals("AZ4", new Cell(51, 3, 0).ref());
        assertEquals("BA5", new Cell(52, 4, 0).ref());
        assertEquals("BZ6", new Cell(77, 5, 0).ref());
        assertEquals("CA7", new Cell(78, 6, 0).ref());
        assertEquals("DV8", new Cell(125, 7, 0).ref());
        assertEquals("ABW101", new Cell(750, 100, 0).ref());
        assertEquals("ALL129", new Cell(999, 128, 0).ref());
        assertEquals("ALU1", new Cell(1008, 0, 0).ref());
    }

    @Test
    void testToXml() {
        final var sharedStrings = new SharedStrings();
        assertEquals("<c r=\"A1\" t=\"s\"><v>0</v></c>", new Cell(0, 0, 0).value(sharedStrings.cache("STRING 0")).toXml());
        assertEquals("<c r=\"A2\" t=\"s\"><v>1</v></c>", new Cell(0, 1, 0).value(sharedStrings.cache("STRING 1")).toXml());
        assertEquals("<c r=\"B1\" t=\"s\"><v>0</v></c>", new Cell(1, 0, 0).value(sharedStrings.cache("STRING 0")).toXml());

        assertEquals("<c r=\"B2\"/>", new Cell(1, 1, 0).toXml());

        assertEquals("<c r=\"A3\" t=\"n\"><v>5</v></c>", new Cell(0, 2, 0).value(Integer.valueOf(5)).toXml());
        assertEquals("<c r=\"C1\" t=\"n\"><v>983</v></c>", new Cell(2, 0, 0).value(Long.valueOf(983L)).toXml());
        assertEquals("<c r=\"C3\" t=\"n\"><v>3.14</v></c>", new Cell(2, 2, 0).value(Double.valueOf(3.14)).toXml());

        assertEquals("<c r=\"C3\"><f>SUM(C1:C3)</f></c>", new Cell(2, 2, 0).value(new Formula("SUM(C1:C3)")).toXml());
    }
}
