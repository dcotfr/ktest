package fr.dcotton.ktest.kafka.avrogen;

import fr.dcotton.ktest.core.KTestException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class UnionConverter implements AvroTypeConverter {
    private final JsonToAvroReader jsonToAvroReader;

    public UnionConverter(final JsonToAvroReader pJsonToAvroReader) {
        jsonToAvroReader = pJsonToAvroReader;
    }

    @Override
    public Object convert(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath) {
        List<Schema> types = pSchema.getTypes();
        List<String> incompatibleTypes = new ArrayList<>();
        for (Schema type : types) {
            try {
                Object nestedValue = this.jsonToAvroReader.read(pField, type, pJsonValue, pPath, true);
                if (nestedValue instanceof Incompatible) {
                    incompatibleTypes.add(((Incompatible) nestedValue).expected());
                } else {
                    return nestedValue;
                }
            } catch (AvroRuntimeException e) {
                // thrown only for union of more complex types like records
                continue;
            }
        }
        throw unionException(pField.name(), String.join(", ", incompatibleTypes), pPath);
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.UNION);
    }

    private static KTestException unionException(final String pFieldName, final String pExpectedTypes, final Deque<String> pOffendingPath) {
        return new KTestException("Could not evaluate union, field " +
                pFieldName +
                " is expected to be one of these: " +
                pExpectedTypes +
                ". If this is a complex type, check if offending field: " + pOffendingPath + " adheres to schema.", null);
    }
}