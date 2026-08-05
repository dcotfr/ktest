package ktest.kafka.jsongen;

import com.fasterxml.jackson.databind.JsonNode;
import io.confluent.kafka.schemaregistry.json.JsonSchemaUtils;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class JsonConverter {
    public Object toJsonSerializable(final JsonNode pJsonNode, final JsonNode pJsonSchema) {
        if (pJsonNode == null || pJsonNode.isNull() || pJsonNode.isTextual()) {
            return null;
        }
        return JsonSchemaUtils.envelope(pJsonSchema, pJsonNode);
    }
}