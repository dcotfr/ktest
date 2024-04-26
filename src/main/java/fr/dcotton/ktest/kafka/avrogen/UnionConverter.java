package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Deque;

@ApplicationScoped
final class UnionConverter implements AvroTypeConverter {
    @Inject
    private JsonToAvroReader jsonToAvroReader;

    @Override
    public Object convert(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath) {
        final var types = pSchema.getTypes();
        final var incompatibleTypes = new ArrayList<String>();
        for (final Schema type : types) {
            try {
                final var nestedValue = jsonToAvroReader.read(pField, type, pJsonValue, pPath);
                if (nestedValue instanceof Incompatible incomp) {
                    incompatibleTypes.add(incomp.expected());
                } else {
                    return nestedValue;
                }
            } catch (final AvroRuntimeException e) {
                // thrown only for union of more complex types like records
            }
        }
        throw new AvroGenException("Could not evaluate union, field " +
                pField.name() +
                " is expected to be one of these: " +
                String.join(", ", incompatibleTypes) +
                ". If this is a complex type, check if offending field: " + pPath + " adheres to schema.");
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.UNION);
    }
}