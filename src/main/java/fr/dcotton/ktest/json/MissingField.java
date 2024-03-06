package fr.dcotton.ktest.json;

public record MissingField(String field) implements Failure {
    @Override
    public String message() {
        return "Missing field '" + field + "'.";
    }
}
