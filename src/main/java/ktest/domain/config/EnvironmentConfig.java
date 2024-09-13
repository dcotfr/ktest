package ktest.domain.config;

import ktest.domain.Named;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record EnvironmentConfig(String name, String onStart, String onEnd) implements Named {
    public List<String> onStartScript() {
        return onStart != null ? Arrays.stream(onStart.split("\n")).toList() : Collections.emptyList();
    }

    public List<String> onEndScript() {
        return onEnd != null ? Arrays.stream(onEnd.split("\n")).toList() : Collections.emptyList();
    }
}
