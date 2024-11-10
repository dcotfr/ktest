package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static ktest.domain.xlsx.Border.Style.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BorderTest {
    @Test
    void testToXml() {
        assertEquals("<border diagonalUp=\"false\" diagonalDown=\"false\"/>", new Border(NONE, NONE, NONE, NONE).toXml());
        assertEquals("<border diagonalUp=\"false\" diagonalDown=\"false\"><left style=\"thin\"><color rgb=\"FF000000\"/></left><right style=\"dotted\"><color rgb=\"FF000000\"/></right></border>", new Border(THIN, DOTTED, NONE, NONE).toXml());
        assertEquals("<border diagonalUp=\"false\" diagonalDown=\"false\"><top style=\"dotted\"><color rgb=\"FF000000\"/></top><bottom style=\"thin\"><color rgb=\"FF000000\"/></bottom></border>", new Border(NONE, NONE, DOTTED, THIN).toXml());
    }
}
