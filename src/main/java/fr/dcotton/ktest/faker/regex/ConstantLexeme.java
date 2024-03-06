package fr.dcotton.ktest.faker.regex;

final class ConstantLexeme extends RepeatableLexeme<ConstantLexeme> {
    private final String constant;

    ConstantLexeme(final String pConstant) {
        constant = pConstant != null ? pConstant : "";
    }

    @Override
    protected String randomUnit() {
        return constant;
    }
}
