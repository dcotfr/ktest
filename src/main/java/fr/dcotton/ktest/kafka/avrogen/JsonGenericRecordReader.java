package fr.dcotton.ktest.kafka.avrogen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.Map;

@ApplicationScoped
final class JsonGenericRecordReader {
    private final ObjectMapper mapper;
    private final JsonToAvroReader jsonToAvroReader;

    @Inject
    JsonGenericRecordReader(final ObjectMapper pMapper, final JsonToAvroReader pJsonToAvroReader) {
        mapper = pMapper;
        jsonToAvroReader = pJsonToAvroReader;
    }

    GenericData.Record read(final JsonNode pJsonNode, final Schema pSchema) {
        try {
            return jsonToAvroReader.read(mapper.convertValue(pJsonNode, Map.class), pSchema);
        } catch (final AvroRuntimeException e) {
            throw new AvroGenException("Failed to convert JSON to Avro.", e);
        }
    }
}