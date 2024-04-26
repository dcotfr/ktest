package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
final class LongTimestampMillisConverter extends LongDateTimeConverter {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    Object convertDateTimeString(final String pDateTimeString) {
        return parseInstant(pDateTimeString).toEpochMilli();
    }

    Instant parseInstant(final String pDateTimeString) {
        return Instant.from(dateTimeFormatter.parse(pDateTimeString));
    }

    @Override
    LogicalType getLogicalType() {
        return LogicalTypes.timestampMillis();
    }
}