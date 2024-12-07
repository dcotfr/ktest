package ktest.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ktest.core.KTestException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.TreeMap;

public final class FoundRecord {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @JsonProperty
    final String topic;
    @JsonProperty
    final int partition;
    @JsonProperty
    final long offset;
    @JsonProperty
    final long timestamp;
    @JsonProperty
    final int keySize;
    @JsonProperty
    final int valueSize;
    @JsonProperty
    final Map<String, String> headers;
    @JsonProperty
    final Object key;
    @JsonProperty
    final Object value;

    public FoundRecord(final ConsumerRecord<?, ?> pRecord) {
        topic = pRecord.topic();
        partition = pRecord.partition();
        offset = pRecord.offset();
        timestamp = pRecord.timestamp();
        keySize = pRecord.serializedKeySize();
        valueSize = pRecord.serializedValueSize();
        headers = new TreeMap<>();
        for (final var h : pRecord.headers()) {
            headers.put(h.key(), new String(h.value()));
        }
        key = toInternalJson(pRecord.key());
        value = toInternalJson(pRecord.value());
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
