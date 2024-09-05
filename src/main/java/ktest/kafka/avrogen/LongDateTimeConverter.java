package ktest.kafka.avrogen;

import org.apache.avro.Schema;

abstract class LongDateTimeConverter extends DateTimeConverter {
    @Override
    final Object convertNumber(final Number pNumberValue) {
        return pNumberValue.longValue();
    }

    @Override
    final Schema.Type getUnderlyingSchemaType() {
        return Schema.Type.LONG;
    }
}