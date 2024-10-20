package ktest.domain.xlsx;

record Formula(String expression) {
    static Formula sum(final Range pRange) {
        return new Formula("SUM(" + pRange + ')');
    }
}
