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
public class Info extends Func {
    private static final Logger LOG = LoggerFactory.getLogger(Info.class);

    protected Info() {
        super("info", new FuncDoc(FuncType.LOG, "\"r=\", 2*3", "r=6", "Logs the concatenation of evaluated expression(s) as INFO output."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var res = extractUnboundParamsAsTxt(pContext, pParam);
        LOG.info("{}", res.value());
        return res;
    }
}
