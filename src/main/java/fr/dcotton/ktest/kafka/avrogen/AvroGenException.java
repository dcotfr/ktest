package fr.dcotton.ktest.kafka.avrogen;

import fr.dcotton.ktest.core.KTestException;

public final class AvroGenException extends KTestException {
    public AvroGenException(final String pMessage) {
        this(pMessage, null);
    }

    public AvroGenException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
