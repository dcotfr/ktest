package ktest.script.token;

public abstract class Comp<T> extends Token<T> {
    public static final Int TRUE_COMP = new Int(1);
    public static final Int FALSE_COMP = new Int(0);

    Comp(final T pValue) {
        super(2, pValue);
    }
}
