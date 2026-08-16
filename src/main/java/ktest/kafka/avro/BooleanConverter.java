package ktest.kafka.avro;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class BooleanConverter extends PrimitiveConverter<Boolean> {
    BooleanConverter() {
        super(Schema.Type.BOOLEAN, Boolean.class, bool -> bool);
    }
}
