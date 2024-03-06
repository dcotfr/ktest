package fr.dcotton.ktest.faker.regex;

import fr.dcotton.ktest.faker.FakerException;

import java.util.Arrays;

final class RegexStyleLexeme extends RepeatableLexeme<RegexStyleLexeme> {
    private static final String NULL_CHAR_SEQUENCE = String.valueOf((char) 0);
    private final char[] characters;

    RegexStyleLexeme(final String pRegexStyleCharList) throws FakerException {
        characters = pRegexStyleCharList != null ? expandRegexStyleCharList(pRegexStyleCharList) : new char[0];
    }

    @Override
    protected String randomUnit() {
        final var characterCount = characters.length;
        return characterCount > 0 ? String.valueOf(characters[rnd.nextInt(characterCount)]) : "";
    }

    static char[] expandRegexStyleCharList(final String pRegexStyleCharList) throws FakerException {
        if ((pRegexStyleCharList == null) || pRegexStyleCharList.isEmpty()) {
            return new char[0];
        }

        final var expandedChars = new StringBuilder();
        final var splittedSections = pRegexStyleCharList.replaceAll("\\\\-", NULL_CHAR_SEQUENCE).split("-");
        final var nbSplit = splittedSections.length - 1;
        for (int i = 0; i <= nbSplit; i++) {
            splittedSections[i] = splittedSections[i].replaceAll(NULL_CHAR_SEQUENCE, "-");
        }
        for (int i = 0; i <= nbSplit; i++) {
            if (i != nbSplit) {
                final var startSection = splittedSections[i];
                final var startChar = startSection.charAt(startSection.length() - 1);
                final var endSection = splittedSections[i + 1];
                final var endChar = endSection.charAt(0);
                if (endChar < startChar) {
                    throw new FakerException("Invalid range: '" + startChar + '-' + endChar + "', start is after end.");
                }
                expandedChars.append(startSection);
                for (var c = startChar; c < endChar; c++) {
                    expandedChars.append(c);
                }
                expandedChars.append(endSection);
            } else {
                expandedChars.append(splittedSections[i]);
            }
        }

        final var sortedChars = expandedChars.toString().toCharArray();
        Arrays.sort(sortedChars);

        final var deduplicatedChars = new StringBuilder();
        var previous = 0;
        for (var c : sortedChars) {
            if (previous != c) {
                deduplicatedChars.append(c);
            }
            previous = c;
        }

        return deduplicatedChars.toString().toCharArray();
    }
}
