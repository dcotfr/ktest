package fr.dcotton.ktest.domain.config;

import fr.dcotton.ktest.domain.Named;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record EnvironmentConfig(String name, String onStart) implements Named {
    public List<String> onStartScript() {
        return onStart != null ? Arrays.stream(onStart.split("\n")).toList() : Collections.emptyList();
    }
}
