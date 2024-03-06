package fr.dcotton.ktest.core;

public class KTestException extends RuntimeException {
    public KTestException(final String pMessage, final Throwable pThrowable) {
        super(pMessage, pThrowable);
    }
}
