package fr.dcotton.ktest.kafka.avrogen;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

final class LongTimestampMillisConverter extends AbstractLongDateTimeConverter {
    public static final AvroTypeConverter INSTANCE = new LongTimestampMillisConverter(DateTimeFormatter.ISO_DATE_TIME);

    private final DateTimeFormatter dateTimeFormatter;

    private LongTimestampMillisConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    protected Object convertDateTimeString(String pDateTimeString) {
        return parseInstant(pDateTimeString).toEpochMilli();
    }

    protected Instant parseInstant(String dateTimeString) {
        return Instant.from(dateTimeFormatter.parse(dateTimeString));
    }

    @Override
    protected LogicalType getLogicalType() {
        return LogicalTypes.timestampMillis();
    }

    @Override
    protected String getValidStringFormat() {
        return "date time";
    }

    @Override
    protected String getValidNumberFormat() {
        return "timestamp";
    }
}