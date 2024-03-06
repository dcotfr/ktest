package fr.dcotton.ktest.script.func;

import fr.dcotton.ktest.faker.UuidFaker;
import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Token;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FakerUuid extends Func {
    private static final UuidFaker FAKER = new UuidFaker();

    protected FakerUuid() {
        super("faker.uuid", new FuncDoc("", "\"fd48147a-58ba-461b-b71c-f44c89ba67ca\"", "Returns a new random UUID."));
    }

    @Override
    public Token apply(final Context pContext, final Stm pParam) {
        extractParam(pContext, pParam);
        return new Txt(FAKER.random());
    }
}
