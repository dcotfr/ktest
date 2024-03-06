package fr.dcotton.ktest.faker.regex;

import fr.dcotton.ktest.faker.FakerException;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static fr.dcotton.ktest.faker.regex.RegexStyleLexeme.expandRegexStyleCharList;
import static org.junit.jupiter.api.Assertions.*;

class RegexStyleLexemeTest extends AbstractLexemeTest<RegexStyleLexeme> {
    @Test
    void testExpandRegexStyleCharListNullOrEmpty() throws FakerException {
        assertArrayEquals(new char[0], expandRegexStyleCharList(null));
        assertArrayEquals(new char[0], expandRegexStyleCharList(""));
    }

    @Test
    void testExpandRegexStyleCharListSimpleEnum() throws FakerException {
        assertEquals("a", String.valueOf(expandRegexStyleCharList("a")));
        assertEquals("#0=D_g~", String.valueOf(expandRegexStyleCharList("#0=D_g~")));
        assertEquals("$&56@JKL`hi|", String.valueOf(expandRegexStyleCharList("|hi`LKJ@56&$")));
    }

    @Test
    void testExpandRegexStyleCharListDeduplicate() throws FakerException {
        assertEquals(" ", String.valueOf(expandRegexStyleCharList("  ")));
        assertEquals("123", String.valueOf(expandRegexStyleCharList("122333")));
        assertEquals("ablrsv", String.valueOf(expandRegexStyleCharList("vbvrlsaal")));
    }

    @Test
    void testExpandRegexStyleEscapedHyphen() throws FakerException {
        assertEquals(" -0Z", String.valueOf(expandRegexStyleCharList(" Z\\-0")));
        assertEquals("-ab", String.valueOf(expandRegexStyleCharList("\\-ab")));
        assertEquals("-DF", String.valueOf(expandRegexStyleCharList("DF\\-")));
    }

    @Test
    void testExpandRegexStyleCharListRange() throws FakerException {
        assertEquals("0123456789", String.valueOf(expandRegexStyleCharList("0-9")));
        assertEquals("abcdefghijklmnopqrstuvwxyz", String.valueOf(expandRegexStyleCharList("a-z")));
        assertEquals("A", String.valueOf(expandRegexStyleCharList("A-A")));
    }

    @Test
    void testExpandRegexStyleCharListCombination() throws FakerException {
        assertEquals("2345ghijkl", String.valueOf(expandRegexStyleCharList("2-5g-l")));
        assertEquals("aefghijklmpst", String.valueOf(expandRegexStyleCharList("sample-list")));
    }

    @Test
    void testRandomNullOrEmptyConstant() throws FakerException {
        assertRandomMatchRegex("", new RegexStyleLexeme(null));
        assertRandomMatchRegex("", new RegexStyleLexeme(""));
    }

    @Test
    void testRandomDefaultRepetition() throws FakerException {
        assertRandomMatchRegex("a", new RegexStyleLexeme("a"));
        assertRandomMatchRegex("[dmz]", new RegexStyleLexeme("dmz"));
    }

    @Test
    void testRandomSimpleRepetition() throws FakerException {
        assertRandomMatchRegex("a{0,2}", new RegexStyleLexeme("a").repetitionLimits(0, 2));
        assertRandomMatchRegex("[bc]{2,5}", new RegexStyleLexeme("bc").repetitionLimits(2, 5));
        assertRandomMatchRegex("[0-9]{3}", new RegexStyleLexeme("0123456789").repetitionLimits(3, 3));
    }

    @Test
    void testRandomRangeRepetition() throws FakerException {
        assertRandomMatchRegex("a{1,2}", new RegexStyleLexeme("a-a").repetitionLimits(1, 2));
        assertRandomMatchRegex("[a-cD-F7-9]{2,5}", new RegexStyleLexeme("a-cD-E7-9").repetitionLimits(2, 5));
        assertRandomMatchRegex("[0-9]{3}", new RegexStyleLexeme("0-9").repetitionLimits(3, 3));
    }

    @Test
    void testRandomReachUnexpectedChar() throws FakerException {
        assertRandomMatchUnexpectedChar("[bc]", new RegexStyleLexeme("a-c"));
        assertRandomMatchUnexpectedChar("[0A]", new RegexStyleLexeme("0A\\-"));
    }

    @Test
    void testInvalidRange() {
        assertEquals("Invalid range: '9-0', start is after end.",
                assertThrowsExactly(FakerException.class, () -> new RegexStyleLexeme("9-0")).getMessage());
    }

    @Override
    protected RepeatableLexeme<RegexStyleLexeme> getTestLimitLexeme() throws FakerException {
        return new RegexStyleLexeme("a");
    }

    private void assertRandomMatchUnexpectedChar(final String pRegexExpected, final RegexStyleLexeme pLexeme) {
        final var pattern = Pattern.compile(pRegexExpected);
        var unexpectedChar = false;
        for (int i = 0; i < 100; i++) {
            final var random = pLexeme.random();
            if (!pattern.matcher(random).matches()) {
                unexpectedChar = true;
                break;
            }
        }
        assertTrue(unexpectedChar, "No expected 'unexpected char' during test");
    }
}
