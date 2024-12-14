package ktest.script;

import io.quarkus.arc.All;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import ktest.kafka.FoundRecord;
import ktest.script.func.Func;
import ktest.script.token.Flt;
import ktest.script.token.Int;
import ktest.script.token.Token;
import ktest.script.token.Txt;

import java.util.*;

@Dependent
public final class Context {
    private final Map<String, Func> functions = new TreeMap<>();
    private final Map<String, Token> variables = new TreeMap<>();
    private FoundRecord lastRecord;
    private boolean pauseDisabled;

    @Inject
    @All
    private List<Func> funcs;

    @PostConstruct
    void init() {
        funcs.forEach(f -> functions.put(f.command(), f));
    }

    Context reset() {
        lastRecord = null;
        variables.clear();
        return this;
    }

    Context init(final Collection<Map.Entry<String, Token>> pVariables) {
        reset();
        if (pVariables != null) {
            pVariables.forEach(entry -> variables.put(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    public Context disablePause(final boolean pFlag) {
        pauseDisabled = pFlag;
        return this;
    }

    public Context lastRecord(final FoundRecord pRecord) {
        lastRecord = pRecord;
        return this;
    }

    public FoundRecord lastRecord() {
        return lastRecord;
    }

    public Collection<Func> functions() {
        return new ArrayList<>(functions.values());
    }

    public Collection<Map.Entry<String, Token>> variables() {
        return new ArrayList<>(variables.entrySet());
    }

    public Func function(final String pName) {
        return functions.get(pName);
    }

    public boolean pauseDisabled() {
        return pauseDisabled;
    }

    public Token variable(final String pName) {
        return variables.get(pName);
    }

    public Context variable(final String pName, final Token pValue) {
        variables.put(pName, pValue);
        return this;
    }

    public Context variable(final String pName, final Long pValue) {
        return variable(pName, new Int(pValue));
    }

    public Context variable(final String pName, final Double pValue) {
        return variable(pName, new Flt(pValue));
    }

    public Context variable(final String pName, final String pValue) {
        return variable(pName, new Txt(pValue));
    }
}
