package fr.dcotton.ktest.domain;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collections;
import java.util.Map;

public record Record(Long timestamp, Map<String, String> headers, JsonNode key, JsonNode value) {
    public Map<String, String> headers() {
        return headers != null ? headers : Collections.emptyMap();
    }
}
