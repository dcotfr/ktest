package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilderBase;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecordBuilder;

import java.util.Deque;
import java.util.Map;

final class RecordConverter extends AvroTypeConverterWithStrictJavaTypeCheck<Map> {
    private final JsonToAvroReader jsonToAvroReader;

    RecordConverter(final JsonToAvroReader pJsonToAvroReader) {
        super(Map.class);
        jsonToAvroReader = pJsonToAvroReader;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertValue(final Schema.Field pField, final Schema pSchema, final Map pJsonValue, final Deque<String> pPath, final boolean pLenient) {
        final var builder = createRecordBuilder(pSchema);
        ((Map<String, Object>) pJsonValue).forEach((key, value) -> {
            final var subField = pSchema.getField(key);
            if (subField != null) {
                final var fieldValue = jsonToAvroReader.read(subField, subField.schema(), value, pPath, pLenient);
                setField(builder, subField, fieldValue);
            }
        });
        return builder.build();
    }

    RecordBuilderBase<GenericData.Record> createRecordBuilder(final Schema pSchema) {
        return new GenericRecordBuilder(pSchema);
    }

    void setField(final RecordBuilderBase<GenericData.Record> pBuilder, final Schema.Field pSubField, final Object pFieldValue) {
        ((GenericRecordBuilder) pBuilder).set(pSubField, pFieldValue);
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.RECORD);
    }
}
