package ktest.core;

public final class Strings {
    private Strings() {
    }

    public static boolean isNullOrEmpty(final String pString) {
        return pString == null || pString.trim().isEmpty();
    }

    public static String repeat(final String pModel, final int pCount) {
        final var res = new StringBuilder();
        res.append(String.valueOf(pModel).repeat(Math.max(0, pCount)));
        return res.toString();
    }
}
