package ktest.script;

import ktest.core.KTestException;

public class ScriptException extends KTestException {
    public ScriptException(final String pMessage) {
        this(pMessage, null);
    }

    public ScriptException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
