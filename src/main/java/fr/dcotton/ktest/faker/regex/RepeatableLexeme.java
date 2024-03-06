package fr.dcotton.ktest.faker.regex;

import fr.dcotton.ktest.faker.FakerException;

import java.util.Random;

abstract class RepeatableLexeme<T> {
    protected final Random rnd = new Random();

    private int minRepetition = 1;
    private int maxRepetition = 1;

    protected RepeatableLexeme() {
        // No specific
    }

    final T repetitionLimits(final int pMin, final int pMax) throws FakerException {
        if (pMin < 0) {
            throw new FakerException("Invalid repetition limit '" + pMin + "': must be positive.");
        }
        if (pMin > pMax) {
            throw new FakerException("Invalid repetition limits: lower '" + pMin + "' is greater than upper '" + pMax + "'.");
        }
        minRepetition = pMin;
        maxRepetition = pMax;
        return (T) this;
    }

    final String random() {
        final var res = new StringBuilder();
        final var limitRange = maxRepetition - minRepetition + 1;
        var repetitionCount = rnd.nextInt(limitRange) + minRepetition;
        while (repetitionCount > 0) {
            res.append(randomUnit());
            repetitionCount--;
        }

        return res.toString();
    }

    protected abstract String randomUnit();
}