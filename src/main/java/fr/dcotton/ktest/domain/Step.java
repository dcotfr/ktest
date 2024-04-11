package fr.dcotton.ktest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RegisterForReflection
public record Step(String name, String before,
                   @JsonProperty(required = true) String broker, @JsonProperty(required = true) String topic,
                   @JsonProperty(required = true) Action action, @JsonProperty(required = true) Record record,
                   String after) implements Named {
    public List<String> beforeScript() {
        return before != null ? Arrays.stream(before.split("\n")).toList() : Collections.emptyList();
    }

    public List<String> afterScript() {
        return after != null ? Arrays.stream(after.split("\n")).toList() : Collections.emptyList();
    }
}
