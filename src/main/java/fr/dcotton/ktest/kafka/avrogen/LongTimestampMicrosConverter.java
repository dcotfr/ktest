package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
final class LongTimestampMicrosConverter extends LongDateTimeConverter {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    Object convertDateTimeString(final String pDateTimeString) {
        final var instant = parseInstant(pDateTimeString);
        // based on org.apache.avro.data.TimestampMicrosConversion
        final var seconds = instant.getEpochSecond();
        final var nanos = instant.getNano();
        if (seconds < 0 && nanos > 0) {
            final var micros = Math.multiplyExact(seconds + 1, 1_000_000);
            final var adjustment = (nanos / 1_000L) - 1_000_000;
            return Math.addExact(micros, adjustment);
        }
        final var micros = Math.multiplyExact(seconds, 1_000_000);
        return Math.addExact(micros, nanos / 1_000);
    }

    Instant parseInstant(final String pDateTimeString) {
        return Instant.from(dateTimeFormatter.parse(pDateTimeString));
    }

    @Override
    LogicalType getLogicalType() {
        return LogicalTypes.timestampMicros();
    }
}