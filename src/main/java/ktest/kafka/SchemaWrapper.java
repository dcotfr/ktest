package ktest.kafka;

import org.apache.avro.Schema;

/**
 * Wrapper for schema that can be either Avro or JSON Schema.
 * Currently only Avro schema is supported. JSON Schema support is planned.
 */
public class SchemaWrapper {
    private final Schema avroSchema;
    private final String rawJsonSchema;
    private final Serde serde;

    public static SchemaWrapper ofAvro(final Schema pAvroSchema) {
        return new SchemaWrapper(pAvroSchema, null, Serde.AVRO);
    }

    public static SchemaWrapper ofJson(final String pJsonSchema) {
        return new SchemaWrapper(null, pJsonSchema, Serde.JSON);
    }

    private SchemaWrapper(final Schema pAvroSchema, final String pJsonSchema, final Serde pSerde) {
        avroSchema = pAvroSchema;
        rawJsonSchema = pJsonSchema;
        serde = pSerde;
    }

    public Schema avroSchema() {
        return avroSchema;
    }

    public String jsonSchema() {
        return rawJsonSchema;
    }

    public Serde serde() {
        return serde;
    }

    public boolean isAvro() {
        return avroSchema != null;
    }

    public boolean isJson() {
        return rawJsonSchema != null;
    }
}