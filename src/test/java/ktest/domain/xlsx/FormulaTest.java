package ktest.domain.xlsx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormulaTest {
    @Test
    void testSum() {
        assertEquals(new Formula("SUM(B2)"), Formula.sum(new Range(1, 1, 1, 1)));
        assertEquals(new Formula("SUM(C3:E3)"), Formula.sum(new Range(2, 2, 4, 2)));
    }
}
