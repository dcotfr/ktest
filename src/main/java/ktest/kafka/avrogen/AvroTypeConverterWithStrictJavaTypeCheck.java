package ktest.kafka.avrogen;

import org.apache.avro.Schema;

import java.util.Deque;

abstract class AvroTypeConverterWithStrictJavaTypeCheck<T> implements AvroTypeConverter {
    private final Class<?> javaType;

    AvroTypeConverterWithStrictJavaTypeCheck(final Class<?> pJavaType) {
        javaType = pJavaType;
    }

    @Override
    public final Object convert(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath) {
        if (javaType.isInstance(pJsonValue)) {
            return convertValue(pField, pSchema, (T) pJsonValue, pPath);
        }
        throw new AvroGenException("Field " + pPath + " is expected to be type: " + javaType.getTypeName());
    }

    abstract Object convertValue(final Schema.Field pField, final Schema pSchema, final T pValue, final Deque<String> pPath);
}