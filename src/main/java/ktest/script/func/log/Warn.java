package ktest.script.func.log;

import ktest.script.Context;
import ktest.script.ScriptException;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.func.FuncType;
import ktest.script.token.Stm;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Warn extends Func {
    private static final Logger LOG = LoggerFactory.getLogger(Warn.class);

    protected Warn() {
        super("warn", new FuncDoc(FuncType.LOG, "2+3", "5", "Display the evaluated expression as WARN log output."));
    }

    @Override
    public Stm apply(final Context pContext, final Stm pParam) {
        final var found = pParam.value().size();
        if (found != 1) {
            throw new ScriptException(STR."Invalid number of arguments in \{command()}: 1 expected, \{found} found.");
        }
        LOG.info("{}", ((Stm) pParam.value().getFirst()).evalValue(pContext));
        return pParam;
    }
}
