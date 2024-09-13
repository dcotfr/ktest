package ktest.script.func.log;

import jakarta.enterprise.context.ApplicationScoped;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.func.FuncType;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class Debug extends Func {
    private static final Logger LOG = LoggerFactory.getLogger(Debug.class);

    protected Debug() {
        super("debug", new FuncDoc(FuncType.LOG, "2+3", "5", "Logs the concatenation of evaluated expression(s) as DEBUG output."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var res = extractUnboundParamsAsTxt(pContext, pParam);
        LOG.debug("{}", res.value());
        return res;
    }
}
