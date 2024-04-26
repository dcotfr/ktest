package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;

abstract class IntDateTimeConverter extends DateTimeConverter {
    @Override
    final Object convertNumber(final Number pNumberValue) {
        return pNumberValue.intValue();
    }

    @Override
    final Schema.Type getUnderlyingSchemaType() {
        return Schema.Type.INT;
    }
}