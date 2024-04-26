package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;

import java.util.Collection;
import java.util.Deque;

import static java.util.stream.Collectors.toList;

final class ArrayConverter extends AvroTypeConverterWithStrictJavaTypeCheck<Collection> {
    private final JsonToAvroReader jsonToAvroReader;

    ArrayConverter(final JsonToAvroReader pJsonToAvroReader) {
        super(Collection.class);
        jsonToAvroReader = pJsonToAvroReader;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertValue(final Schema.Field pField, final Schema pSchema, final Collection pValue, final Deque<String> pPath) {
        return ((Collection<Object>) pValue).stream()
                .map(item -> jsonToAvroReader.read(pField, pSchema.getElementType(), item, pPath))
                .collect(toList());
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.ARRAY);
    }
}