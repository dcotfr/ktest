package ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
final class IntTimeMillisConverter extends IntDateTimeConverter {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_TIME;

    @Override
    Object convertDateTimeString(final String pDateTimeString) {
        final var nanoOfDay = parseLocalTime(pDateTimeString).toNanoOfDay();
        return TimeUnit.NANOSECONDS.toMillis(nanoOfDay);
    }

    LocalTime parseLocalTime(final String pDateTimeString) {
        return LocalTime.from(dateTimeFormatter.parse(pDateTimeString));
    }

    @Override
    LogicalType getLogicalType() {
        return LogicalTypes.timeMillis();
    }
}
