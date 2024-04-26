package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
final class BytesConverter extends PrimitiveConverter<String> {
    BytesConverter() {
        super(Schema.Type.BYTES, String.class, value -> ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8)));
    }
}
