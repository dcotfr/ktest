package fr.dcotton.ktest.faker.regex;

import fr.dcotton.ktest.faker.FakerException;
import org.junit.jupiter.api.Test;

class ConstantLexemeTest extends AbstractLexemeTest<ConstantLexeme> {
    @Test
    void testRandomNullOrEmptyConstant() {
        assertRandomMatchRegex("", new ConstantLexeme(null));
        assertRandomMatchRegex("", new ConstantLexeme(""));
    }

    @Test
    void testRandomDefaultRepetition() {
        assertRandomMatchRegex("a", new ConstantLexeme("a"));
        assertRandomMatchRegex("test", new ConstantLexeme("test"));
    }

    @Test
    void testRandomSimpleRepetition() throws FakerException {
        assertRandomMatchRegex("a{0,2}", new ConstantLexeme("a").repetitionLimits(0, 2));
        assertRandomMatchRegex("b{2,5}", new ConstantLexeme("b").repetitionLimits(2, 5));
        assertRandomMatchRegex("ccc", new ConstantLexeme("c").repetitionLimits(3, 3));
    }

    @Override
    protected RepeatableLexeme<ConstantLexeme> getTestLimitLexeme() {
        return new ConstantLexeme("a");
    }
}
