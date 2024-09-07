package ktest.json;

public record MissingField(String field) implements Failure {
    @Override
    public String message() {
        return STR."Missing field '\{field}'.";
    }
}
