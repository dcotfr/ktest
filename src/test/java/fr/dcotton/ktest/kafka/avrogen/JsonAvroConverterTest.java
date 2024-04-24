package fr.dcotton.ktest.kafka.avrogen;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@QuarkusTest
class JsonAvroConverterTest {
    @Inject
    ObjectMapper mapper;

    @Inject
    JsonAvroConverter converter;

    @Test
    void convertToGenericDataRecordTest() throws IOException {
        final var jsonNode = mapper.readTree("""
                {
                  "sender": "Source",
                  "eventType": "CREATE",
                  "eventTsp": 1713648197189,
                  "body": {
                    "code": "P1",
                    "label": "Product 1",
                    "commandAt": 1713648196189,
                    "weight":12030.5
                  }
                }""");
        final var schema = new Schema.Parser().parse("""
                {"type":"record","name":"InputTopicValue","namespace":"fr.dcotton.ktest","fields":[{"name":"sender","type":"string"},{"name":"eventType","type":"string"},{"name":"eventTsp","type":{"type":"long","logicalType":"timestamp-millis"}},{"name":"body","type":{"type":"record","name":"Body","fields":[{"name":"code","type":["null","string"],"default":null},{"name":"label","type":["null","string"],"default":null},{"name":"commandAt","type":["null",{"type":"long","logicalType":"timestamp-millis"}],"default":null},{"name":"sentAt","type":["null",{"type":"long","logicalType":"timestamp-millis"}],"default":null},{"name":"weight","type":["null","double"],"default":null}]}}]}
                """);

        final var avro = converter.toAvro(jsonNode, schema);
        System.out.println(avro);
    }
}
