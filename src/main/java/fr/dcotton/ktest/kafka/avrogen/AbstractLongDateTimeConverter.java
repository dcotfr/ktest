package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.Schema;

abstract class AbstractLongDateTimeConverter extends AbstractDateTimeConverter {
    @Override
    protected Object convertNumber(final Number pNumberValue) {
        return pNumberValue.longValue();
    }

    @Override
    protected Schema.Type getUnderlyingSchemaType() {
        return Schema.Type.LONG;
    }
}