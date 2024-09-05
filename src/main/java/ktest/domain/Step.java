package ktest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import ktest.kafka.Serde;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RegisterForReflection
public record Step(String name, String before,
                   @JsonProperty(required = true) String broker, @JsonProperty(required = true) String topic,
                   Serde keySerde, Serde valueSerde,
                   @JsonProperty(required = true) Action action,
                   @JsonProperty(required = true) TestRecord record,
                   String after) implements Named {
    public List<String> beforeScript() {
        return before != null ? Arrays.stream(before.split("\n")).toList() : Collections.emptyList();
    }

    public Serde keySerde() {
        return keySerde != null ? keySerde : Serde.STRING;
    }

    public Serde valueSerde() {
        return valueSerde != null ? valueSerde : Serde.AVRO;
    }

    public List<String> afterScript() {
        return after != null ? Arrays.stream(after.split("\n")).toList() : Collections.emptyList();
    }
}
