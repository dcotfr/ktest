package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.faker.regex.RegexStyleFaker;
import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Regexgen extends Func {
    protected Regexgen() {
        super("regexgen", new FuncDoc("\"E-[A-Z]{2,4}#{2}\"", "\"E-AJD##\"", "Returns a new random string matching provided regex."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        final var params = extractParam(pContext, pParam, String.class);
        return new Txt(RegexStyleFaker.build((String) params[0]).random());
    }
}
