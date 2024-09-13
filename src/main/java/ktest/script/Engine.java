package ktest.script;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import ktest.domain.config.KTestConfig;
import ktest.script.token.Token;
import ktest.script.token.Tokenizer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Dependent
public final class Engine {
    @Inject
    private Context context;

    @Inject
    private KTestConfig kConfig;

    public Engine reset() {
        context.reset();
        final var envConfig = kConfig.currentEnvironment();
        if (envConfig != null) {
            envConfig.onStartScript().forEach(this::eval);
        }
        return this;
    }

    public Engine init(final Collection<Map.Entry<String, Token<?>>> pVariables) {
        context.init(pVariables);
        return this;
    }

    public Engine end() {
        final var envConfig = kConfig.currentEnvironment();
        if (envConfig != null) {
            envConfig.onEndScript().forEach(this::eval);
        }
        return this;
    }

    public Object eval(final String pLine) {
        final var stm = new Tokenizer().tokenize(pLine);
        return stm.evalValue(context);
    }

    public void eval(final List<String> pScript) {
        if (pScript != null) {
            pScript.forEach(this::eval);
        }
    }

    public Context context() {
        return context;
    }
}
