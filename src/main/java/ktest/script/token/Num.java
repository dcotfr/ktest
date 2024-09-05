package ktest.script.token;

public abstract class Num<T extends Number> extends Token<T> {
    Num(final T pValue) {
        super(0, pValue);
    }
}
