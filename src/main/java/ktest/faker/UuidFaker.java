package ktest.faker;

import java.util.UUID;

public final class UuidFaker {
    public String random() {
        return UUID.randomUUID().toString();
    }
}
