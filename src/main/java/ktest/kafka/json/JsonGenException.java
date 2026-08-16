package ktest.kafka.json;

import ktest.core.KTestException;

public final class JsonGenException extends KTestException {
    JsonGenException(final String pMessage) {
        this(pMessage, null);
    }

    JsonGenException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
