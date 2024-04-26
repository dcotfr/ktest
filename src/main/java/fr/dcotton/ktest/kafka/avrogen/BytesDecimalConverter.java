package fr.dcotton.ktest.kafka.avrogen;

import fr.dcotton.ktest.core.KTestException;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.avro.Schema;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.Deque;

import static org.apache.avro.Schema.Type.BYTES;

@ApplicationScoped
final class BytesDecimalConverter implements AvroTypeConverter {
    static final AvroTypeConverter INSTANCE = new BytesDecimalConverter();

    @Override
    public Object convert(final Schema.Field pField, final Schema pSchema, final Object pValue, final Deque<String> pPath) {
        try {
            final var scale = (int) pSchema.getObjectProp("scale");
            return convertDecimal(pValue, scale, pPath);
        } catch (final NumberFormatException e) {
            throw new AvroGenException("Field " + pPath + " is expected to be a valid number. current value is " + pValue + '.', e);
        }
    }

    protected Object convertDecimal(final Object pValue, final int scale, final Deque<String> pPath) {
        final var bigDecimal = bigDecimalWithExpectedScale(pValue.toString(), scale, pPath);
        return ByteBuffer.wrap(bigDecimal.unscaledValue().toByteArray());
    }

    protected BigDecimal bigDecimalWithExpectedScale(final String pDecimal, final int pScale, final Deque<String> pPath) {
        final var bigDecimalInput = new BigDecimal(pDecimal);
        if (bigDecimalInput.scale() <= pScale) {
            return bigDecimalInput.setScale(pScale, RoundingMode.UNNECESSARY);
        }
        throw new AvroGenException("Field " + pPath + " is expected to be a number with scale up to " +
                pScale + ". current value: " + bigDecimalInput + " is number with scale " + bigDecimalInput.scale() + '.', null);
    }

    @Override
    public boolean canManage(final Schema pSchema, final Deque<String> pPath) {
        return BYTES.equals(pSchema.getType())
                && AvroTypeConverter.isLogicalType(pSchema, "decimal")
                && pSchema.getObjectProp("scale") != null;
    }
}