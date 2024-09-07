package ktest.json;

public record BadArraySize(String field, String message) implements Failure {
    @Override
    public String message() {
        return STR."Bad array size of field '\{field}': \{message}.";
    }
}
