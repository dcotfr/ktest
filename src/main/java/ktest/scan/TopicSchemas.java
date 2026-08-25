package ktest.scan;

import ktest.kafka.SchemaWrapper;

public record TopicSchemas(SchemaWrapper keySchema, SchemaWrapper valueSchema) {
}
