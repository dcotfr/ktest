package ktest.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.avro.Schema;

record SchemaWrapper(Serde serde, Schema avroSchema, JsonNode jsonSchema) {
    static SchemaWrapper ofAvro(final Schema pAvroSchema) {
        return new SchemaWrapper(Serde.AVRO, pAvroSchema, null);
    }

    static SchemaWrapper ofJson(final JsonNode pJsonSchema) {
        return new SchemaWrapper(Serde.JSON, null, pJsonSchema);
    }
}