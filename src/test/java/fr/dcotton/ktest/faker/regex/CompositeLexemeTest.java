package fr.dcotton.ktest.faker.regex;

import fr.dcotton.ktest.faker.FakerException;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static fr.dcotton.ktest.faker.regex.LexemeType.*;
import static org.junit.jupiter.api.Assertions.*;

class CompositeLexemeTest extends AbstractLexemeTest<CompositeLexeme> {
    @Test
    void testCompositeChildren() throws FakerException {
        final var root = new CompositeLexeme(null)
                .addAndResetLexeme(CONSTANT, new StringBuilder("test"));
        final var child = root.appendNewComposite();
        child.addAndResetLexeme(CONSTANT, new StringBuilder("#"))
                .addAndResetLexeme(REGEX, new StringBuilder("0-9"));
        child.parent().addAndResetLexeme(REPETITION, new StringBuilder("1,3"));
        assertRandomMatchRegex("test(#[0-9]){1,3}", root);
    }

    @Test
    void testRepetitionMissingBase() {
        assertEquals("Invalid repetition: missing base lexeme.",
                assertThrowsExactly(FakerException.class, () -> new CompositeLexeme(null)
                        .addAndResetLexeme(REPETITION, new StringBuilder("1"))).getMessage());
    }

    @Test
    void testRepetitionTooManyArgument() {
        assertEquals("Invalid repetition: too many arguments.",
                assertThrowsExactly(FakerException.class, () -> new CompositeLexeme(null)
                        .addAndResetLexeme(CONSTANT, new StringBuilder("a"))
                        .addAndResetLexeme(REPETITION, new StringBuilder("1,2,3"))).getMessage());
    }

    @Test
    void testRepetitionInvalidLimit() {
        assertEquals("Invalid repetition limits: not a number.",
                assertThrowsExactly(FakerException.class, () -> new CompositeLexeme(null)
                        .addAndResetLexeme(CONSTANT, new StringBuilder("a"))
                        .addAndResetLexeme(REPETITION, new StringBuilder("error"))).getMessage());
    }

    @Test
    void testOrReachUnexpected() throws FakerException {
        final var orCombination = new CompositeLexeme(null)
                .addAndResetLexeme(CONSTANT, new StringBuilder("test1"))
                .appendNewOrLexemes()
                .addAndResetLexeme(CONSTANT, new StringBuilder("test2"))
                .appendNewOrLexemes()
                .addAndResetLexeme(CONSTANT, new StringBuilder("test"))
                .addAndResetLexeme(REGEX, new StringBuilder("3-4"));
        assertRandomMatchUnexpectedOr("test1", orCombination);
        assertRandomMatchUnexpectedOr("test2", orCombination);
        assertRandomMatchUnexpectedOr("test3", orCombination);
    }

    @Override
    protected RepeatableLexeme<CompositeLexeme> getTestLimitLexeme() throws FakerException {
        return new CompositeLexeme(null).addAndResetLexeme(CONSTANT, new StringBuilder("a"));
    }

    private void assertRandomMatchUnexpectedOr(final String pRegexExpected, final CompositeLexeme pLexeme) {
        final var pattern = Pattern.compile(pRegexExpected);
        var unexpectedOr = false;
        for (int i = 0; i < 100; i++) {
            final var random = pLexeme.random();
            if (!pattern.matcher(random).matches()) {
                unexpectedOr = true;
                break;
            }
        }
        assertTrue(unexpectedOr, "No expected 'unexpected or' during test");
    }
}
