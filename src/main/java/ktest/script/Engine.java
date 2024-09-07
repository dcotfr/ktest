package ktest.script;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import ktest.domain.config.KTestConfig;
import ktest.script.token.Tokenizer;

import java.util.List;

@Dependent
public final class Engine {
    @Inject
    private Context context;

    @Inject
    private KTestConfig kConfig;

    @PostConstruct
    void init() {
        reset();
    }

    public Engine reset() {
        context.reset();
        final var envConfig = kConfig.currentEnvironment();
        if (envConfig != null) {
            envConfig.onStartScript().forEach(this::eval);
        }
        return this;
    }

    public Object eval(final String pLine) {
        final var stm = new Tokenizer().tokenize(pLine);
        return stm.evalValue(context);
    }

    public Object eval(final List<String> pScript) {
        Object res = null;
        if (pScript != null) {
            for (final var line : pScript) {
                res = eval(line);
            }
        }
        return res;
    }

    public Context context() {
        return context;
    }
}
