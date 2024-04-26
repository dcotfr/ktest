package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class DoubleConverter extends PrimitiveConverter<Number> {
    DoubleConverter() {
        super(Schema.Type.DOUBLE, Number.class, Number::doubleValue);
    }
}
