package ktest.script.token;

import ktest.script.Context;

/**
 * Add : 2
 * Div : 3
 * Fun : 4
 * Let : 1
 * Mul : 3
 * Num : 0
 * Stm : 5
 * Sub : 2
 * Tex : 0
 * Var : 6
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

    Token<?> eval(final Context pContext, final Stm pStatement) {
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + value();
    }
}
