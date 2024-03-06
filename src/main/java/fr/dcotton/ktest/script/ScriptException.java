package fr.dcotton.ktest.script;

import fr.dcotton.ktest.core.KTestException;

public final class ScriptException extends KTestException {
    public ScriptException(final String pMessage) {
        this(pMessage, null);
    }

    public ScriptException(final String pMessage, final Throwable pCause) {
        super(pMessage, pCause);
    }
}
