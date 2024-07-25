package fr.dcotton.ktest.script.func.log;

import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.ScriptException;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.func.FuncType;
import fr.dcotton.ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Error extends Func {
    private static final Logger LOG = LoggerFactory.getLogger(Error.class);

    protected Error() {
        super("error", new FuncDoc(FuncType.LOG, "2+3", "5", "Display the evaluated expression as ERROR log output."));
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
