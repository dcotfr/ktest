package ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
final class IntDateConverter extends IntDateTimeConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    Object convertDateTimeString(final String pDateTimeString) {
        return parseLocalDate(pDateTimeString).toEpochDay();
    }

    LocalDate parseLocalDate(final String pDateTimeString) {
        return LocalDate.from(DATE_TIME_FORMATTER.parse(pDateTimeString));
    }

    @Override
    LogicalType getLogicalType() {
        return LogicalTypes.date();
    }
}