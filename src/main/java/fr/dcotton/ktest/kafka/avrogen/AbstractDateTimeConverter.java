package fr.dcotton.ktest.kafka.avrogen;

import fr.dcotton.ktest.core.KTestException;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.time.format.DateTimeParseException;
import java.util.Deque;

abstract class AbstractDateTimeConverter implements AvroTypeConverter {
    @Override
    public Object convert(final Schema.Field pField, final Schema pSchema, final Object pJsonValue, final Deque<String> pPath) {
        if (pJsonValue instanceof String dateTimeString) {
            try {
                return convertDateTimeString(dateTimeString);
            } catch (DateTimeParseException e) {
                throw new KTestException("Field " + pPath + " should be a valid " + getValidStringFormat() + '.', e);
            }
        } else if (pJsonValue instanceof Number nbr) {
            return convertNumber(nbr);
        }

        throw new KTestException("Field " + pPath + " is expected to be type: java.lang.String or java.lang.Number.", null);
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return getUnderlyingSchemaType().equals(pSchema.getType()) && AvroTypeConverter.isLogicalType(pSchema, getLogicalType().getName());
    }

    protected abstract Object convertDateTimeString(final String pDateTimeString);

    protected abstract Object convertNumber(final Number pNumberValue);

    protected abstract Schema.Type getUnderlyingSchemaType();

    protected abstract LogicalType getLogicalType();

    private String getValidJsonFormat() {
        return getValidStringFormat() + " string, " + getValidNumberFormat() + " number";
    }

    protected abstract String getValidStringFormat();

    protected abstract String getValidNumberFormat();
}