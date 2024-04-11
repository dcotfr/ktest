package fr.dcotton.ktest.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import fr.dcotton.ktest.core.KTestException;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.Map;

@RegisterForReflection
public record Record(Long timestamp, Map<String, String> headers, String key, String value) {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    public Map<String, String> headers() {
        return headers != null ? headers : Collections.emptyMap();
    }

    public JsonNode keyNode() {
        try {
            return toJsonNode(key);
        } catch (final JsonProcessingException e) {
            throw new KTestException("Syntax of record key is invalid.", e);
        }
    }

    public JsonNode valueNode() {
        try {
            return toJsonNode(value);
        } catch (final JsonProcessingException e) {
            throw new KTestException("Syntax of record value is invalid.", e);
        }
    }

    private static JsonNode toJsonNode(final String pString) throws JsonProcessingException {
        return MAPPER.readValue(pString, JsonNode.class);
    }

    @Override
    public String toString() {
        final var res = new StringBuilder("{");
        if (timestamp != null) {
            res.append("\"timestamp\":").append(timestamp);
        }
        if (!headers.isEmpty()) {
            res.append(res.length() > 1 ? "," : "").append("\"headers\":[");
            var comma = false;
            for (final var e : headers.entrySet()) {
                res.append(comma ? "," : "").append('"').append(e.getKey()).append("\":\"").append(e.getValue()).append('"');
                comma = true;
            }
            res.append(']');
        }
        if (key != null) {
            res.append(res.length() > 1 ? "," : "").append("\"key\":").append(keyNode());
        }
        if (value != null) {
            res.append(res.length() > 1 ? "," : "").append("\"value\":").append(valueNode());
        }
        res.append('}');
        return res.toString();
    }
}
