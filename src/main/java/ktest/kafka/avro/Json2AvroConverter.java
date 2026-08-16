package ktest.kafka.avro;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

// https://github.com/allegro/json-avro-converter/blob/master/converter/src/main/java/tech/allegro/schema/json2avro/converter/types/RecordConverter.java
@ApplicationScoped
public final class Json2AvroConverter {
    private final JsonGenericRecordReader recordReader;

    @Inject
    public Json2AvroConverter(final JsonGenericRecordReader pRecordReader) {
        recordReader = pRecordReader;
    }

    public GenericData.Record toAvro(final JsonNode pJsonNode, final Schema pSchema) {
        if (pJsonNode == null || pJsonNode.isNull()) {
            return null;
        }
        if (pJsonNode.isTextual()) {
            throw new AvroGenException("Cannot serialize scalar JSON value '" + pJsonNode + "' as Avro record '" + (pSchema.getFullName() == null ? pSchema.getName() : pSchema.getFullName()) + "'", null);
        }
        if (pSchema == null) {
            throw new AvroGenException("Cannot serialize JSON value '" + pJsonNode + "' because of missing Avro schema");
        }
        try {
            return recordReader.read(pJsonNode, pSchema);
        } catch (final Exception e) {
            throw new AvroGenException("Failed to convert JSON value '" + pJsonNode + "' to Avro message '" + (pSchema.getFullName() == null ? pSchema.getName() : pSchema.getFullName()), e);
        }
    }
}