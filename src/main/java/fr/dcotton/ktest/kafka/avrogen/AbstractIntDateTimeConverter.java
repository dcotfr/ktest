package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;

abstract class AbstractIntDateTimeConverter extends AbstractDateTimeConverter {
    @Override
    protected Object convertNumber(final Number pNumberValue) {
        return pNumberValue.intValue();
    }

    @Override
    protected Schema.Type getUnderlyingSchemaType() {
        return Schema.Type.INT;
    }
}