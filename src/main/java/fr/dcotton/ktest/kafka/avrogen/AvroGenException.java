package fr.dcotton.ktest.kafka.avrogen;

import fr.dcotton.ktest.core.KTestException;

public final class AvroGenException extends KTestException {
    AvroGenException(final String pMessage) {
        this(pMessage, null);
    }

    AvroGenException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
