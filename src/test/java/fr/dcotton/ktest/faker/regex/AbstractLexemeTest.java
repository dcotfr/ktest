package fr.dcotton.ktest.faker.regex;

import fr.dcotton.ktest.faker.FakerException;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractLexemeTest<T extends RepeatableLexeme<?>> {
    protected abstract RepeatableLexeme<T> getTestLimitLexeme() throws FakerException;

    @Test
    void testRandomNoOrNegativeRepetition() throws FakerException {
        assertRandomMatchRegex("", getTestLimitLexeme().repetitionLimits(0, 0));
        assertEquals("Invalid repetition limit '-1': must be positive.",
                assertThrowsExactly(FakerException.class, () -> getTestLimitLexeme().repetitionLimits(-1, 0)).getMessage());
        assertEquals("Invalid repetition limits: lower '0' is greater than upper '-1'.",
                assertThrowsExactly(FakerException.class, () -> getTestLimitLexeme().repetitionLimits(0, -1)).getMessage());
    }

    @Test
    void testRandomSimpleReachLowerLimit() throws FakerException {
        assertRandomMatchLimit(4, getTestLimitLexeme().repetitionLimits(4, 5));
    }

    @Test
    void testRandomSimpleReachUpperLimit() throws FakerException {
        assertRandomMatchLimit(5, getTestLimitLexeme().repetitionLimits(4, 5));
    }

    protected final void assertRandomMatchRegex(final String pRegexExpected, final T pLexeme) {
        final var pattern = Pattern.compile(pRegexExpected);
        for (int i = 0; i < 100; i++) {
            final var random = pLexeme.random();
            if (!pattern.matcher(random).matches()) {
                fail('\'' + random + "' does not match expected regex '" + pRegexExpected + "'");
            }
        }
    }

    protected final void assertRandomMatchLimit(final int pLimit, final T pLexeme) {
        var limitReached = false;
        for (int i = 0; i < 100; i++) {
            if (pLexeme.random().length() == pLimit) {
                limitReached = true;
                break;
            }
        }
        assertTrue(limitReached, "Limit not reached during test");
    }
}
