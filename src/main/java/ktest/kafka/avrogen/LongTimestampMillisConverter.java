package ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
final class LongTimestampMillisConverter extends LongDateTimeConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    Object convertDateTimeString(final String pDateTimeString) {
        return parseInstant(pDateTimeString).toEpochMilli();
    }

    Instant parseInstant(final String pDateTimeString) {
        return Instant.from(DATE_TIME_FORMATTER.parse(pDateTimeString));
    }

    @Override
    LogicalType getLogicalType() {
        return LogicalTypes.timestampMillis();
    }
}