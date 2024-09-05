package ktest.kafka.avrogen;

import org.apache.avro.Schema;

import java.util.Deque;

interface AvroTypeConverter {
    Object convert(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath);

    boolean canManage(final Schema pSchema, final Deque<String> pPath);

    static boolean isLogicalType(final Schema pSchema, final String pLogicalType) {
        return pSchema.getLogicalType() != null && pLogicalType.equals(pSchema.getLogicalType().getName());
    }

    record Incompatible(String expected) {
    }
}