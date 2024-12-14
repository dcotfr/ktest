package ktest.script;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import ktest.domain.config.KTestConfig;
import ktest.script.token.Token;
import ktest.script.token.Tokenizer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    public Engine init(final Collection<Map.Entry<String, Token>> pVariables) {
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
        Object res = null;
        for (final var stm : new Tokenizer().tokenize(pLine)) {
            res = stm.evalValue(context);
        }
        return res;
    }

    public void eval(final List<String> pScript) {
        if (pScript != null) {
            pScript.forEach(this::eval);
        }
    }

    public String evalInLine(final String pAttribute) {
        if (pAttribute == null) {
            return null;
        }

        final var pattern = Pattern.compile("(\\$\\{[^}]*+})");
        final var matcher = pattern.matcher(pAttribute);
        var res = pAttribute;
        while (matcher.find()) {
            final var group = matcher.group();
            final var repl = eval(group.substring(2, group.length() - 1)).toString();
            res = res.replace(group, repl);
        }
        return res;
    }

    public Context context() {
        return context;
    }
}
