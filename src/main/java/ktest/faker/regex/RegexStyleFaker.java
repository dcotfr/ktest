package ktest.faker.regex;

import ktest.faker.FakerException;

import static ktest.faker.regex.LexemeType.*;

public final class RegexStyleFaker {
    private final String config;
    private final CompositeLexeme rootLexeme;

    private RegexStyleFaker(final String pRegexStyleConfig) {
        config = pRegexStyleConfig != null ? pRegexStyleConfig : "";
        rootLexeme = new CompositeLexeme(null);
    }

    public String config() {
        return config;
    }

    public String random() {
        return rootLexeme.random();
    }

    private CompositeLexeme rootLexeme() {
        return rootLexeme;
    }

    public static RegexStyleFaker build(final String pRegexStyleConfig) throws FakerException {
        final var res = new RegexStyleFaker(pRegexStyleConfig);
        var previousChar = 0;
        var lexemeType = CONSTANT;
        var root = res.rootLexeme();
        final var lexeme = new StringBuilder();
        for (var c : res.config().toCharArray()) {
            if (c == '[') {
                if (previousChar != '\\') {
                    if (lexemeType == REGEX) {
                        throw new FakerException("Invalid range: duplicated '['.");
                    }
                    root.addAndResetLexeme(lexemeType, lexeme);
                    lexemeType = REGEX;
                    continue;
                }
            } else if (c == ']') {
                if (previousChar != '\\') {
                    if (lexemeType != REGEX) {
                        throw new FakerException("Invalid range: missing '['.");
                    }
                    root.addAndResetLexeme(lexemeType, lexeme);
                    lexemeType = CONSTANT;
                    continue;
                }
            } else if (c == '{') {
                if (previousChar != '\\') {
                    if (lexemeType == REPETITION) {
                        throw new FakerException("Invalid repetition: duplicated '{'.");
                    }
                    root.addAndResetLexeme(lexemeType, lexeme);
                    lexemeType = REPETITION;
                    continue;
                }
            } else if (c == '}') {
                if (previousChar != '\\') {
                    if (lexemeType != REPETITION) {
                        throw new FakerException("Invalid repetition: missing '{'.");
                    }
                    root.addAndResetLexeme(lexemeType, lexeme);
                    lexemeType = CONSTANT;
                    continue;
                }
            } else if (c == '(') {
                if (previousChar != '\\') {
                    root = root.appendNewComposite();
                    continue;
                }
            } else if (c == ')') {
                if (previousChar != '\\') {
                    root = root.parent();
                    continue;
                }
            } else if (c == '|') {
                if ((previousChar != '\\') && (lexemeType == CONSTANT)) {
                    root.appendNewOrLexemes();
                    continue;
                }
            }
            if (c != '\\') {
                if ((previousChar == '\\') && (lexemeType == REGEX)) {
                    lexeme.append('\\');
                }
                lexeme.append(c);
            }
            if (lexemeType == CONSTANT) {
                root.addAndResetLexeme(lexemeType, lexeme);
            }
            previousChar = c;
        }
        if (lexemeType != CONSTANT) {
            throw new FakerException(lexemeType == REGEX ? "Invalid range: missing ']'." : "Invalid repetition: missing '}'.");
        }
        root.addAndResetLexeme(lexemeType, lexeme);
        return res;
    }
}
