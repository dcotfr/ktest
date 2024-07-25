package fr.dcotton.ktest.script.func.faker;

import fr.dcotton.ktest.faker.UuidFaker;
import fr.dcotton.ktest.script.Context;
import fr.dcotton.ktest.script.func.Func;
import fr.dcotton.ktest.script.func.FuncDoc;
import fr.dcotton.ktest.script.token.Stm;
import fr.dcotton.ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static fr.dcotton.ktest.script.func.FuncType.FAKER;

@ApplicationScoped
public class Uuid extends Func {
    private static final UuidFaker UUID_FAKER = new UuidFaker();

    protected Uuid() {
        super("uuid", new FuncDoc(FAKER, "", "\"fd48147a-58ba-461b-b71c-f44c89ba67ca\"", "Returns a new random UUID."));
    }

    @Override
    public Txt apply(final Context pContext, final Stm pParam) {
        extractParam(pContext, pParam);
        return new Txt(UUID_FAKER.random());
    }
}
