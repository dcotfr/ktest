package ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.Schema;

import java.util.Collection;
import java.util.Deque;

@ApplicationScoped
final class ArrayConverter extends AvroTypeConverterWithStrictJavaTypeCheck<Collection<?>> {
    @Inject
    private JsonToAvroReader jsonToAvroReader;

    ArrayConverter() {
        super(Collection.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertValue(final Schema.Field pField, final Schema pSchema, final Collection<?> pValue, final Deque<String> pPath) {
        return pValue.stream()
                .map(item -> jsonToAvroReader.read(pField, pSchema.getElementType(), item, pPath)).toList();
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.ARRAY);
    }
}