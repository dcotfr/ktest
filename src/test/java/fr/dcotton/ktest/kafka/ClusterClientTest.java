package fr.dcotton.ktest.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.schemaregistry.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaUtils;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@QuarkusTest
class ClusterClientTest {
    @Inject
    ObjectMapper mapper;

    @Test
    void toAvroRecordTest() throws JsonProcessingException, IOException {
        final var jsonNode = mapper.readTree("""
                {
                  "sender": "Source",
                  "eventType": "CREATE",
                  "eventTsp": 1713648197189,
                  "body": {
                    "code": "P1",
                    "label": "Product 1",
                    "commandAt": 1713648196189,
                    "sentAt": 1713648196289,
                    "weight":12030.5
                  }
                }""");
        final var schema = new Schema.Parser().parse("""
                {"type":"record","name":"InputTopicValue","namespace":"fr.dcotton.ktest","fields":[{"name":"sender","type":"string"},{"name":"eventType","type":"string"},{"name":"eventTsp","type":{"type":"long","logicalType":"timestamp-millis"}},{"name":"body","type":{"type":"record","name":"Body","fields":[{"name":"code","type":["null","string"],"default":null},{"name":"label","type":["null","string"],"default":null},{"name":"commandAt","type":["null",{"type":"long","logicalType":"timestamp-millis"}],"default":null},{"name":"sentAt","type":["null",{"type":"long","logicalType":"timestamp-millis"}],"default":null},{"name":"weight","type":["null","double"],"default":null}]}}]}
                """);

        System.out.println(AvroSchemaUtils.toObject(jsonNode,new AvroSchema("{\"type\":\"record\",\"name\":\"InputTopicValue\",\"namespace\":\"fr.dcotton.ktest\",\"fields\":[{\"name\":\"sender\",\"type\":\"string\"},{\"name\":\"eventType\",\"type\":\"string\"},{\"name\":\"eventTsp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"body\",\"type\":{\"type\":\"record\",\"name\":\"Body\",\"fields\":[{\"name\":\"code\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"label\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"commandAt\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],\"default\":null},{\"name\":\"sentAt\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],\"default\":null},{\"name\":\"weight\",\"type\":[\"null\",\"double\"],\"default\":null}]}}]}")));

        final var decoderFactory = DecoderFactory.get();
        final var jsonString = jsonNode.toString();
        try {
            DatumReader<Object> reader = new GenericDatumReader<Object>(schema);
            Object object = reader.read(null, decoderFactory.jsonDecoder(schema, jsonString));

            if (schema.getType().equals(Schema.Type.STRING)) {
                object = object.toString();
            }
            System.out.println(object);
        } finally {

        }
        //ClusterClient.toAvroRecord(jsonNode, schema);
    }
}
