package fr.dcotton.ktest.faker;

import fr.dcotton.ktest.core.KTestException;

public final class FakerException extends KTestException {
    public FakerException(final String pMessage) {
        this(pMessage, null);
    }

    public FakerException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
