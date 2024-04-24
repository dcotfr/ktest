package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

import java.util.Deque;

@ApplicationScoped
final class NullConverter implements AvroTypeConverter {
    static final NullConverter INSTANCE = new NullConverter();

    @Override
    public Object convert(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath) {
        return pJsonValue == null ? null : new Incompatible("NULL");
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.NULL);
    }
}