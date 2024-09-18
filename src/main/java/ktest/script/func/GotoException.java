package ktest.script.func;

import ktest.script.ScriptException;

public final class GotoException extends ScriptException {
    private final String stepName;

    GotoException(final String pStepName) {
        super("Goto step " + pStepName);
        stepName = pStepName;
    }

    public String stepName() {
        return stepName;
    }
}
