package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.function.Function;


final class PrimitiveConverter<T> extends AvroTypeConverterWithStrictJavaTypeCheck<T> {
    public final static AvroTypeConverter BOOLEAN = new PrimitiveConverter<>(Schema.Type.BOOLEAN, Boolean.class, bool -> bool);
    public final static AvroTypeConverter STRING = new PrimitiveConverter<>(Schema.Type.STRING, String.class, string -> string);
    public final static AvroTypeConverter INT = new PrimitiveConverter<>(Schema.Type.INT, Number.class, Number::intValue);
    public final static AvroTypeConverter LONG = new PrimitiveConverter<>(Schema.Type.LONG, Number.class, Number::longValue);
    public final static AvroTypeConverter DOUBLE = new PrimitiveConverter<>(Schema.Type.DOUBLE, Number.class, Number::doubleValue);
    public final static AvroTypeConverter FLOAT = new PrimitiveConverter<>(Schema.Type.FLOAT, Number.class, Number::floatValue);
    public final static AvroTypeConverter BYTES = new PrimitiveConverter<>(Schema.Type.BYTES, String.class, value -> ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8)));

    private final Schema.Type avroType;
    private final Function<T, Object> mapper;

    protected PrimitiveConverter(final Schema.Type pAvroType, final Class<T> pJavaType, final Function<T, Object> pMapper) {
        super(pJavaType);
        avroType = pAvroType;
        mapper = pMapper;
    }

    @Override
    public Object convertValue(final Schema.Field pField, final Schema pSchema, final T pValue, final Deque<String> pPath) {
        return mapper.apply(pValue);
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(avroType);
    }
}