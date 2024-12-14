package ktest.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ktest.core.KTestException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public final class FoundRecord {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty
    private final String topic;
    @JsonProperty
    private final int partition;
    @JsonProperty
    private final long offset;
    @JsonProperty
    private final long timestamp;
    @JsonProperty
    private final int keySize;
    @JsonProperty
    private final int valueSize;
    @JsonProperty
    private final Map<String, String> headers;
    @JsonProperty
    private final Object key;
    @JsonProperty
    private final Object value;

    public FoundRecord(final ConsumerRecord<?, ?> pRecord) {
        topic = pRecord.topic();
        partition = pRecord.partition();
        offset = pRecord.offset();
        timestamp = pRecord.timestamp();
        keySize = pRecord.serializedKeySize();
        valueSize = pRecord.serializedValueSize();
        headers = new TreeMap<>();
        for (final var h : pRecord.headers()) {
            headers.put(h.key(), new String(h.value(), StandardCharsets.UTF_8));
        }
        key = toInternalJson(pRecord.key());
        value = toInternalJson(pRecord.value());
    }

    public Map<String, String> headers() {
        return headers;
    }

    public Object key() {
        return key;
    }

    public Object value() {
        return value;
    }

    private static Object toInternalJson(final Object pObject) {
        final var str = pObject != null ? pObject.toString() : null;
        if (str == null) {
            return null;
        }
        if ((str.startsWith("{") && str.endsWith("}")) || (str.startsWith("[") && str.endsWith("]"))) {
            try {
                return MAPPER.readTree(str);
            } catch (final JsonProcessingException e) {
                throw new KTestException("Failed to create FoundRecord.", e);
            }
        }
        return str;
    }

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (final JsonProcessingException e) {
            throw new KTestException("Failed to serialize FoundRecord.", e);
        }
    }
}
