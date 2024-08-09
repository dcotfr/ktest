package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.Schema;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
final class MapConverter extends AvroTypeConverterWithStrictJavaTypeCheck<Map> {
    @Inject
    private JsonToAvroReader recordRecord;

    MapConverter() {
        super(Map.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertValue(final Schema.Field pField, final Schema pSchema, final Map pJsonValue, final Deque<String> pPath, final boolean pLenient) {
        final var result = new HashMap<>(pJsonValue.size());
        ((Map<String, Object>) pJsonValue).forEach((k, v) ->
                result.put(k, recordRecord.read(pField, pSchema.getValueType(), v, pPath, pLenient))
        );
        return result;
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.MAP);
    }
}