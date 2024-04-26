package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;

import java.util.Deque;
import java.util.function.Function;

class PrimitiveConverter<T> extends AvroTypeConverterWithStrictJavaTypeCheck<T> {
    private final Schema.Type avroType;
    private final Function<T, Object> mapper;

    PrimitiveConverter(final Schema.Type pAvroType, final Class<T> pJavaType, final Function<T, Object> pMapper) {
        super(pJavaType);
        avroType = pAvroType;
        mapper = pMapper;
    }

    @Override
    public final Object convertValue(final Schema.Field pField, final Schema pSchema, final T pValue, final Deque<String> pPath) {
        return mapper.apply(pValue);
    }

    @Override
    public final boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(avroType);
    }
}