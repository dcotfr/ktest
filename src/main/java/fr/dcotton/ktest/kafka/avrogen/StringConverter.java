package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

@ApplicationScoped
final class StringConverter extends PrimitiveConverter<String> {
    StringConverter() {
        super(Schema.Type.STRING, String.class, string -> string);
    }
}
