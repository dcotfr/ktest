package ktest.script;

import ktest.script.func.Func;
import ktest.script.token.Flt;
import ktest.script.token.Int;
import ktest.script.token.Token;
import ktest.script.token.Txt;
import io.quarkus.arc.All;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.util.*;

@Dependent
public final class Context {
    private final Map<String, Func> functions = new TreeMap<>();
    private final Map<String, Token<?>> variables = new TreeMap<>();

    @Inject
    @All
    private List<Func> funcs;

    @PostConstruct
    void init() {
        funcs.forEach(f -> functions.put(f.command(), f));
    }

    Context reset() {
        variables.clear();
        return this;
    }

    public Collection<Func> functions() {
        return functions.values();
    }

    public Set<Map.Entry<String, Token<?>>> variables() {
        return variables.entrySet();
    }

    public Func function(final String pName) {
        return functions.get(pName);
    }

    public Token<?> variable(final String pName) {
        return variables.get(pName);
    }

    public Context variable(final String pName, final Token<?> pValue) {
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
