package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class IntConverter extends PrimitiveConverter<Number> {
    IntConverter() {
        super(Schema.Type.INT, Number.class, Number::intValue);
    }
}
