package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
final class LongTimeMicrosConverter extends AbstractLongDateTimeConverter {
    static final AvroTypeConverter INSTANCE = new LongTimeMicrosConverter();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_TIME;

    @Override
    protected Object convertDateTimeString(final String pDateTimeString) {
        final var nanoOfDay = parseLocalTime(pDateTimeString).toNanoOfDay();
        return TimeUnit.NANOSECONDS.toMicros(nanoOfDay);
    }

    protected LocalTime parseLocalTime(final String pDateTimeString) {
        return LocalTime.from(dateTimeFormatter.parse(pDateTimeString));
    }

    @Override
    protected LogicalType getLogicalType() {
        return LogicalTypes.timeMicros();
    }

    @Override
    protected String getValidStringFormat() {
        return "time";
    }

    @Override
    protected String getValidNumberFormat() {
        return "micros";
    }
}