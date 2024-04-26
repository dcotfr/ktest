package fr.dcotton.ktest.kafka.avrogen;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.Deque;

import static java.util.stream.Collectors.joining;

@ApplicationScoped
final class EnumConverter extends AvroTypeConverterWithStrictJavaTypeCheck<String> {
    static final AvroTypeConverter INSTANCE = new EnumConverter();

    private EnumConverter() {
        super(String.class);
    }

    @Override
    public Object convertValue(final Schema.Field pField, final Schema pSchema, final String pValue, final Deque<String> pPath) {
        final var symbols = pSchema.getEnumSymbols();
        if (symbols.contains(pValue)) {
            return new GenericData.EnumSymbol(pSchema, pValue);
        }
        throw new AvroGenException("Field " + pPath + " is expected to be of enum type and be one of " + symbols.stream().map(String::valueOf).collect(joining(", ")));
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return pSchema.getType().equals(Schema.Type.ENUM);
    }
}