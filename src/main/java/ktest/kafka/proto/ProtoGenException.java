package ktest.kafka.proto;

import ktest.core.KTestException;

public final class ProtoGenException extends KTestException {
    ProtoGenException(final String pMessage) {
        this(pMessage, null);
    }

    ProtoGenException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
