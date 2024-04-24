package fr.dcotton.ktest.kafka.avrogen;

import fr.dcotton.ktest.core.KTestException;
import org.apache.avro.Schema;

import java.util.Deque;

abstract class AvroTypeConverterWithStrictJavaTypeCheck<T> implements AvroTypeConverter {
    private final Class<T> javaType;

    protected AvroTypeConverterWithStrictJavaTypeCheck(final Class<T> pJavaType) {
        javaType = pJavaType;
    }

    @Override
    public Object convert(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath) {
        if (javaType.isInstance(pJsonValue)) {
            return convertValue(pField, pSchema, (T) pJsonValue, pPath);
        }
        throw new KTestException("Field " + pPath + " is expected to be type: " + javaType.getTypeName(), null);
    }

    abstract Object convertValue(final Schema.Field pField, final Schema pSchema, final T pValue, final Deque<String> pPath);
}