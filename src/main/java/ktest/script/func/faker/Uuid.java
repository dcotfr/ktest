package ktest.script.func.faker;

import ktest.faker.UuidFaker;
import ktest.script.Context;
import ktest.script.func.Func;
import ktest.script.func.FuncDoc;
import ktest.script.token.Stm;
import ktest.script.token.Txt;
import jakarta.enterprise.context.ApplicationScoped;

import static ktest.script.func.FuncType.FAKER;

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
