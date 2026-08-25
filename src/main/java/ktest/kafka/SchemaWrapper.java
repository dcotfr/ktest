package ktest.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.protobuf.Descriptors;
import org.apache.avro.Schema;

public sealed interface SchemaWrapper {
    default Serde serde() {
        return switch (this) {
            case Avro _ -> Serde.AVRO;
            case Json _ -> Serde.JSON;
            case Protobuf _ -> Serde.PROTOBUF;
        };
    }

    static SchemaWrapper ofAvro(final Schema pAvroSchema) {
        return new Avro(pAvroSchema);
    }

    static SchemaWrapper ofJson(final JsonNode pJsonSchema) {
        return new Json(pJsonSchema);
    }

    static SchemaWrapper ofProtobuf(final Descriptors.Descriptor pProtobufDescriptor) {
        return new Protobuf(pProtobufDescriptor);
    }

    record Avro(Schema schema) implements SchemaWrapper {
    }

    record Json(JsonNode schema) implements SchemaWrapper {
    }

    record Protobuf(Descriptors.Descriptor schema) implements SchemaWrapper {
    }
}
