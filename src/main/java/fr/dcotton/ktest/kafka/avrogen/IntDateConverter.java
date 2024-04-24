package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
final class IntDateConverter extends AbstractIntDateTimeConverter {
    static final AvroTypeConverter INSTANCE = new IntDateConverter();

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE;

    @Override
    protected Object convertDateTimeString(final String pDateTimeString) {
        return parseLocalDate(pDateTimeString).toEpochDay();
    }

    protected LocalDate parseLocalDate(final String pDateTimeString) {
        return LocalDate.from(dateTimeFormatter.parse(pDateTimeString));
    }

    @Override
    protected LogicalType getLogicalType() {
        return LogicalTypes.date();
    }

    @Override
    protected String getValidStringFormat() {
        return "date";
    }

    @Override
    protected String getValidNumberFormat() {
        return "epoch days";
    }
}