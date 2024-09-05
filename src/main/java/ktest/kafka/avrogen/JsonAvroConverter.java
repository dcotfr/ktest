package ktest.kafka.avrogen;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

// https://github.com/allegro/json-avro-converter/blob/master/converter/src/main/java/tech/allegro/schema/json2avro/converter/types/RecordConverter.java
@ApplicationScoped
public final class JsonAvroConverter {
    private final JsonGenericRecordReader recordReader;

    @Inject
    public JsonAvroConverter(final JsonGenericRecordReader pRecordReader) {
        recordReader = pRecordReader;
    }

    public GenericData.Record toAvro(final JsonNode pJsonNode, final Schema pSchema) {
        return recordReader.read(pJsonNode, pSchema);
    }
}