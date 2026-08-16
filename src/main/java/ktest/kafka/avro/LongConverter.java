package ktest.kafka.avro;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class LongConverter extends PrimitiveConverter<Number> {
    LongConverter() {
        super(Schema.Type.LONG, Number.class, Number::longValue);
    }
}
