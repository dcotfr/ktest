package ktest.faker.regex;

import ktest.faker.FakerException;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RegexStyleFakerTest {
    @Test
    void testRandomNullOrEmpty() {
        assertRandomMatchRegex("", null);
        assertRandomMatchRegex("", "");
    }

    @Test
    void testRandomSingleFixed() {
        assertRandomMatchRegex("constant1", "constant1");
        assertRandomMatchRegex("fixed-string", "fixed-string");
    }

    @Test
    void testRandomEscapedFixed() {
        assertRandomMatchRegex("\\[fixed\\]", "\\[fixed\\]");
        assertRandomMatchRegex("test\\[ fix\\]ed", "test\\[ fix\\]ed");
    }

    @Test
    void testRandomEscapedRepetition() {
        assertRandomMatchRegex("\\{50\\}", "\\{50\\}");
        assertRandomMatchRegex("test\\{3\\}ed", "test\\{3\\}ed");
    }

    @Test
    void testRandomEscapedComposite() {
        assertRandomMatchRegex("\\(as\\)", "\\(as\\)");
        assertRandomMatchRegex("test\\(fix\\)ed", "test\\(fix\\)ed");
    }

    @Test
    void testRandomSingleRegexStyle() {
        assertRandomMatchRegex("[0-9]", "[0-9]");
        assertRandomMatchRegex("[0-9A-Z]", "[0-9A-Z]");
    }

    @Test
    void testRandomRepeatedSingleFixed() {
        assertRandomMatchRegex("aaa", "a{3}");
        assertRandomMatchRegex("a{3,4}", "a{3,4}");
        assertRandomMatchRegex("ab{1,3}", "ab{1,3}");
    }

    @Test
    void testRandomRepeatedComplexFixed() {
        assertRandomMatchRegex("ababab", "(ab){3}");
        assertRandomMatchRegex("(test){3,4}", "(test){3,4}");
        assertRandomMatchRegex("te(st){1,3}", "te(st){1,3}");
    }

    @Test
    void testRandomOrCombination() {
        assertRandomMatchRegex("(a|b){3}", "(a|b){3}");
        assertRandomMatchRegex("((te)|(st)|(#[0-9]{1,2})){3}|(\\[\\|void\\|\\])",
                "((te)|(st)|(#[0-9]{1,2})){3}|(\\[\\|void\\|\\])");
    }

    @Test
    void testRandomRecursiveComplexFixed() {
        assertRandomMatchRegex("ab(cd(ef){3}[2-8]{2}){1,2}gh", "ab(cd(ef){3}[2-8]{2}){1,2}gh");
    }

    @Test
    void testRandomInvalidRegexStyle() {
        assertEquals("Invalid range: missing ']'.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("[a-z")).getMessage());
        assertEquals("Invalid range: missing '['.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a-z]")).getMessage());
        assertEquals("Invalid range: missing '['.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("[]a-z]")).getMessage());
        assertEquals("Invalid range: duplicated '['.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("[a-z[]")).getMessage());
        assertEquals("Invalid range: missing '['.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("[a-z]]")).getMessage());
    }

    @Test
    void testRandomInvalidRepetitionLimits() {
        assertEquals("Invalid repetition limits: not a number.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{}")).getMessage());
        assertEquals("Invalid repetition limits: lower '3' is greater than upper '2'.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{3,2}")).getMessage());
        assertEquals("Invalid repetition limits: not a number.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{z}")).getMessage());
        assertEquals("Invalid repetition limits: not a number.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{2,-}")).getMessage());
        assertEquals("Invalid repetition limit '-2': must be positive.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{-2}")).getMessage());
        assertEquals("Invalid repetition: too many arguments.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{1,2,3}")).getMessage());
    }

    @Test
    void testRandomInvalidRepetitionSyntax() {
        assertEquals("Invalid repetition: missing base lexeme.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("{2}")).getMessage());
        assertEquals("Invalid repetition: missing '}'.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{3")).getMessage());
        assertEquals("Invalid repetition: missing '{'.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("3}")).getMessage());
        assertEquals("Invalid repetition: missing '{'.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{2}}")).getMessage());
        assertEquals("Invalid repetition: duplicated '{'.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{4{}")).getMessage());
        assertEquals("Invalid repetition: missing '{'.",
                assertThrowsExactly(FakerException.class, () -> RegexStyleFaker.build("a{2,3}}")).getMessage());
    }

    @Test
    void testRandomSingleCombination() {
        assertRandomMatchRegex("tes[tT]", "tes[tT]");
        assertRandomMatchRegex("[fF]ixed", "[fF]ixed");
        assertRandomMatchRegex("tes[tT] fi[xX]ed", "tes[tT] fi[xX]ed");
    }

    @Test
    void testFunctionalUuid() {
        assertRandomMatchRegex("[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}",
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}");
    }

    @Test
    void testFunctionalFrenchPhone() {
        assertRandomMatchRegex("0[0-9](-[0-9]{2}){4}", "0[0-9](-[0-9]{2}){4}");
    }

    @Test
    void testFunctionalEMail() {
        assertRandomMatchRegex("[a-z]{2,9}([._\\-]{0,1}[a-z]{2,9}){0,2}@(((gmail|yahoo|hotmail)\\.com)|((wanadoo|free|sfr)\\.fr))",
                "[a-z]{2,9}([._\\-]{0,1}[a-z]{2,9}){0,2}@(((gmail|yahoo|hotmail).com)|((wanadoo|free|sfr).fr))");
    }

    @Test
    void testFunctionalUrl() {
        assertRandomMatchRegex("http(s){0,1}://www\\.[a-z]{3}(-{0,1}[a-z]{3,7}){1,2}.(com|org|net|tv|us|de|fr|travel)",
                "http(s){0,1}://www.[a-z]{3}(-{0,1}[a-z]{3,7}){1,2}.(com|org|net|tv|us|de|fr|travel)");
    }

    @Test
    void testFunctionalIp() {
        assertRandomMatchRegex("(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])",
                "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])");
    }

    @Test
    void testFunctionalDate() {
        assertRandomMatchRegex("((19|2[01])[0-9]{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]))",
                "((19|2[01])[0-9]{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]))");
    }

    @Test
    void testFunctionalTime() {
        assertRandomMatchRegex("(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]", "(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]");
    }

    private static void assertRandomMatchRegex(final String pRegexExpected, final String pRegexStyleConfig) {
        final var pattern = Pattern.compile(pRegexExpected);
        try {
            final var faker = RegexStyleFaker.build(pRegexStyleConfig);
            for (int i = 0; i < 100; i++) {
                final var random = faker.random();
                if (!pattern.matcher(random).matches()) {
                    fail('\'' + random + "' does not match expected regex '" + pRegexExpected + "'");
                }
            }
        } catch (final FakerException e) {
            fail(e);
        }
    }
}
