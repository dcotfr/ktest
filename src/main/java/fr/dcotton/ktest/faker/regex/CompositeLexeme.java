package fr.dcotton.ktest.faker.regex;

import fr.dcotton.ktest.faker.FakerException;

import java.util.ArrayList;
import java.util.List;

import static fr.dcotton.ktest.faker.regex.LexemeType.REGEX;
import static fr.dcotton.ktest.faker.regex.LexemeType.REPETITION;

final class CompositeLexeme extends RepeatableLexeme<CompositeLexeme> {
    private final CompositeLexeme parent;
    private final List<List<RepeatableLexeme<?>>> orLexemes;

    CompositeLexeme(final CompositeLexeme pParent) {
        parent = pParent;
        orLexemes = new ArrayList<>();
        appendNewOrLexemes();
    }

    CompositeLexeme parent() {
        return parent;
    }

    @Override
    protected String randomUnit() {
        final var res = new StringBuilder();

        for (final var lexeme : orLexemes.get(rnd.nextInt(orLexemes.size()))) {
            res.append(lexeme.random());
        }

        return res.toString();
    }

    private List<RepeatableLexeme<?>> activeLexemes() {
        return orLexemes.get(orLexemes.size() - 1);
    }

    CompositeLexeme appendNewComposite() {
        final var composite = new CompositeLexeme(this);
        activeLexemes().add(composite);
        return composite;
    }

    CompositeLexeme appendNewOrLexemes() {
        orLexemes.add(new ArrayList<>());
        return this;
    }

    CompositeLexeme addAndResetLexeme(final LexemeType pType, final StringBuilder pLemexe) throws FakerException {
        final var lexemeLength = pLemexe.length();
        if (pType == REPETITION) {
            if (activeLexemes().isEmpty()) {
                throw new FakerException("Invalid repetition: missing base lexeme.");
            }
            final var limits = pLemexe.toString().split(",");
            final var nbLimit = limits.length;
            if (nbLimit > 2) {
                throw new FakerException("Invalid repetition: too many arguments.");
            }
            final var min = parseLimit(limits[0]);
            final var max = nbLimit == 1 ? min : parseLimit(limits[1]);
            activeLexemes().get(activeLexemes().size() - 1).repetitionLimits(min, max);
        } else {
            if (lexemeLength > 0) {
                activeLexemes().add(pType == REGEX ? new RegexStyleLexeme(pLemexe.toString()) : new ConstantLexeme(pLemexe.toString()));
            }
        }
        pLemexe.delete(0, lexemeLength);
        return this;
    }

    private int parseLimit(final String pLimit) throws FakerException {
        try {
            return Integer.parseInt(pLimit);
        } catch (final NumberFormatException e) {
            throw new FakerException("Invalid repetition limits: not a number.", e);
        }
    }
}