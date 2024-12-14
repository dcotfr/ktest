package ktest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import ktest.core.KTestException;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collections;
import java.util.Map;

@RegisterForReflection
public final class TestRecord {
    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    @JsonProperty
    private String timestamp;
    @JsonProperty
    private Map<String, String> headers;
    @JsonProperty
    private String key;
    @JsonProperty
    private String value;

    TestRecord() {
    }

    public TestRecord(final String pTimestamp, final Map<String, String> pHeaders, final String pKey, final String pValue) {
        timestamp = pTimestamp;
        headers = pHeaders;
        key = pKey;
        value = pValue;
    }

    public String timestamp() {
        return timestamp;
    }

    public Long longTimestamp() {
        return timestamp != null ? Long.parseLong(timestamp) : null;
    }

    public Map<String, String> headers() {
        return headers != null ? headers : Collections.emptyMap();
    }

    public String key() {
        return key;
    }

    public JsonNode keyNode() {
        try {
            return toJsonNode(key);
        } catch (final JsonProcessingException e) {
            throw new KTestException("Syntax of record key is invalid.", e);
        }
    }

    public String value() {
        return value;
    }

    public JsonNode valueNode() {
        try {
            return toJsonNode(value);
        } catch (final JsonProcessingException e) {
            throw new KTestException("Syntax of record value is invalid.", e);
        }
    }

    private static JsonNode toJsonNode(final String pString) throws JsonProcessingException {
        return pString != null ? MAPPER.readValue(pString, JsonNode.class) : null;
    }

    @Override
    public String toString() {
        final var res = new StringBuilder("{");
        if (timestamp != null) {
            res.append("\"timestamp\":").append(timestamp);
        }
        if (headers != null && !headers.isEmpty()) {
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
