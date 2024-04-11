package fr.dcotton.ktest.script;

import fr.dcotton.ktest.domain.config.KTestConfig;
import fr.dcotton.ktest.script.token.Tokenizer;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public final class Engine {
    private static final Logger LOG = LoggerFactory.getLogger(Engine.class);

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
