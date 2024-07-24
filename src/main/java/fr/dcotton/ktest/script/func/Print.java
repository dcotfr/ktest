package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Print extends Func {
    private static final Logger LOG = LoggerFactory.getLogger(Print.class);

    protected Print() {
        super("print", new FuncDoc("2+3", "5", "Display the evaluated expression as INFO log output."));
    }

    @Override
    public Stm apply(final Context pContext, final Stm pParam) {
        final var found = pParam.value().size();
        if (found != 1) {
            throw new ScriptException("Invalid number of arguments in " + command() + ": 1 expected, " + found + " found.");
        }
        LOG.info("{}", ((Stm) pParam.value().getFirst()).evalValue(pContext));
        return pParam;
    }
}
