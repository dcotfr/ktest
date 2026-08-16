package ktest.kafka.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.confluent.kafka.schemaregistry.json.JsonSchemaUtils;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public final class Json2JsonConverter {
    public ObjectNode toJsonSerializable(final JsonNode pJsonNode, final JsonNode pJsonSchema) {
        if (pJsonNode == null || pJsonNode.isNull()) {
            return null;
        }
        if (pJsonNode.isTextual()) {
            throw new JsonGenException("Cannot serialize scalar JSON value '" + pJsonNode + "' to Json message");
        }
        if (pJsonSchema == null || pJsonSchema.isNull()) {
            throw new JsonGenException("Cannot serialize JSON value '" + pJsonNode + "' because of missing JSON schema");
        }
        try {
            return JsonSchemaUtils.envelope(pJsonSchema, pJsonNode);
        } catch (final Exception e) {
            throw new JsonGenException("Failed to convert JSON value '" + pJsonNode + "' to JSON message '" + pJsonSchema, e);
        }
    }
}