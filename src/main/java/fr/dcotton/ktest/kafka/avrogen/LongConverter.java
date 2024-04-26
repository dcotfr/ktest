package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class LongConverter extends PrimitiveConverter<Number> {
    LongConverter() {
        super(Schema.Type.LONG, Number.class, Number::longValue);
    }
}
