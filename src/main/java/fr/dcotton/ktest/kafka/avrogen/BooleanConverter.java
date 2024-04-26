package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class BooleanConverter extends PrimitiveConverter<Boolean> {
    BooleanConverter() {
        super(Schema.Type.BOOLEAN, Boolean.class, bool -> bool);
    }
}
