package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class FloatConverter extends PrimitiveConverter<Number> {
    FloatConverter() {
        super(Schema.Type.FLOAT, Number.class, Number::floatValue);
    }
}
