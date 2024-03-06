package fr.dcotton.ktest.json;

public record ContentMismatch(String field, String expected, String actual) implements Failure {
    @Override
    public String message() {
        return "Content mismatch of field '" + field + "': '" + expected + "' expected, '" + actual + "' found.";
    }
}
