package ktest.script.token;

import ktest.script.Context;

/**
 * 0: Num (Int, Flt), Txt
 * 1: Let
 * 2: Comp (Eq, Ge, Gt, Le, Lt, Ne)
 * 3: Add, Sub
 * 4: Div, Mul
 * 5: Fun
 * 6: Stm
 * 7: Var
 * 8: If
 *
 * @param <T>
 */
public abstract class Token<T> {
    private final int priority;
    private final T value;

    protected Token(final int pPriority, final T pValue) {
        priority = pPriority;
        value = pValue;
    }

    final int priority() {
        return priority;
    }

    public final T value() {
        return value;
    }

    Token eval(final Context pContext, final Stm pStatement) {
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + value();
    }
}
