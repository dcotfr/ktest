package ktest.script.func.faker;

import ktest.faker.regex.RegexStyleFaker;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.FAKER;

@ApplicationScoped
public class Regexgen extends Func {
    protected Regexgen() {
        super("regexgen", new FuncDoc(FAKER, "\"E-[A-Z]{2,4}#{2}\"", "\"E-AJD##\"", "Returns a new random string matching provided regex."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(RegexStyleFaker.build((String) params[0]).random());
    }
}
