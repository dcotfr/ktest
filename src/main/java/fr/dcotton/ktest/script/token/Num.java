package fr.dcotton.ktest.script.token;

abstract class Num<T extends Number> extends Token<T> {
    Num(final T pValue) {
        super(0, pValue);
    }
}
