package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

final class MapConverter extends AvroTypeConverterWithStrictJavaTypeCheck<Map> {
    private final JsonToAvroReader recordRecord;

    MapConverter(final JsonToAvroReader pRecordRecord) {
        super(Map.class);
        recordRecord = pRecordRecord;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertValue(final Schema.Field pField, final Schema pSchema, final Map pJsonValue, final Deque<String> pPath) {
        final var result = new HashMap<>(pJsonValue.size());
        ((Map<String, Object>) pJsonValue).forEach((k, v) ->
                result.put(k, recordRecord.read(pField, pSchema.getValueType(), v, pPath))
        );
        return result;
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.MAP);
    }
}