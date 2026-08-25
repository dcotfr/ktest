package ktest.mcp;

import io.quarkiverse.mcp.server.ToolCallException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class McpToolsTest {
    @Inject
    McpTools mcpTools;

    @Test
    void testGetDslDocumentation() {
        var res = mcpTools.getDslDocumentation();
        System.out.println(res);
    }

    @Test
    void testGetSchemasOfTopic() {
        var res = mcpTools.getSchemasOfTopic("pi_broker", "InputTopic");
        assertEquals("InputTopic", res.topicName());
        assertEquals("pi_broker", res.brokerId());
        assertNull(res.keySchemaType());
        assertNull(res.keyRawSchema());
        assertEquals("AVRO", res.valueSchemaType());
        assertEquals("{\"type\":\"record\",\"name\":\"InputTopicValue\",\"namespace\":\"ktest\",\"fields\":[{\"name\":\"sender\",\"type\":\"string\"},{\"name\":\"eventType\",\"type\":\"string\"},{\"name\":\"eventTsp\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"body\",\"type\":{\"type\":\"record\",\"name\":\"Body\",\"fields\":[{\"name\":\"code\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"label\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"commandAt\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],\"default\":null},{\"name\":\"sentAt\",\"type\":[\"null\",{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}],\"default\":null},{\"name\":\"weight\",\"type\":[\"null\",\"double\"],\"default\":null}]}}]}", res.valueRawSchema());

        res = mcpTools.getSchemasOfTopic("pi_broker", "InputJsonTopic");
        assertEquals("InputJsonTopic", res.topicName());
        assertEquals("pi_broker", res.brokerId());
        assertEquals("JSON", res.keySchemaType());
        assertEquals("{\"$schema\":\"https://json-schema.org/draft/2020-12/schema\",\"$id\":\"ktest://InputJsonTopic-key.json\",\"type\":\"object\",\"properties\":{\"id\":{\"type\":\"integer\"},\"type\":{\"type\":\"string\",\"enum\":[\"USER\",\"ORDER\",\"PRODUCT\"]}},\"required\":[\"id\",\"type\"]}", res.keyRawSchema());
        assertEquals("JSON", res.valueSchemaType());
        assertEquals("{\"$schema\":\"https://json-schema.org/draft/2020-12/schema\",\"$id\":\"ktest://InputJsonTopic-value.json\",\"type\":\"object\",\"properties\":{\"nonNullableString\":{\"type\":\"string\",\"minLength\":1},\"nonNullableBoolean\":{\"type\":\"boolean\"},\"nonNullableInt\":{\"type\":\"integer\"},\"nonNullableLong\":{\"type\":\"integer\",\"minimum\":-9223372036854775808,\"maximum\":9223372036854775807},\"nonNullableFloat\":{\"type\":\"number\"},\"nonNullableDouble\":{\"type\":\"number\"},\"nonNullableEnum\":{\"type\":\"string\",\"enum\":[\"SEND\",\"PRESENT\",\"ABSENT\",\"TODO\"]},\"nonNullableArrayOfString\":{\"type\":\"array\",\"items\":{\"type\":\"string\"},\"default\":[]},\"nullableString\":{\"type\":[\"null\",\"string\"]},\"nullableBoolean\":{\"type\":[\"null\",\"boolean\"]},\"nullableInt\":{\"type\":[\"null\",\"integer\"]},\"nullableLong\":{\"type\":[\"null\",\"integer\"]},\"nullableFloat\":{\"type\":[\"null\",\"number\"]},\"nullableDouble\":{\"type\":[\"null\",\"number\"]},\"nullableSubRecord\":{\"type\":[\"null\",\"object\"],\"properties\":{\"SubLong\":{\"type\":\"integer\"},\"SubString\":{\"type\":\"string\"},\"nonNullableSubSubRecord\":{\"type\":\"object\",\"properties\":{\"nullableSubSubLong\":{\"type\":[\"null\",\"integer\"]},\"nullableSubSubString\":{\"type\":[\"null\",\"string\"]}},\"required\":[\"nullableSubSubLong\",\"nullableSubSubString\"]}},\"required\":[\"SubLong\",\"SubString\",\"nonNullableSubSubRecord\"]}},\"required\":[\"nonNullableString\",\"nonNullableBoolean\",\"nonNullableInt\",\"nonNullableLong\",\"nonNullableFloat\",\"nonNullableDouble\",\"nonNullableEnum\",\"nonNullableArrayOfString\",\"nullableString\",\"nullableBoolean\",\"nullableInt\",\"nullableLong\",\"nullableFloat\",\"nullableDouble\",\"nullableSubRecord\"]}", res.valueRawSchema());

        res = mcpTools.getSchemasOfTopic("pi_broker", "InputProtobufTopic");
        assertEquals("InputProtobufTopic", res.topicName());
        assertEquals("pi_broker", res.brokerId());
        assertNull(res.keySchemaType());
        assertNull(res.keyRawSchema());
        assertEquals("PROTOBUF", res.valueSchemaType());
        assertEquals("name: \"InputTopicValue\"\nfield {\n  name: \"sender\"\n  number: 1\n  type: TYPE_STRING\n}\nfield {\n  name: \"event_type\"\n  number: 2\n  type: TYPE_STRING\n}\nfield {\n  name: \"body\"\n  number: 3\n  type: TYPE_MESSAGE\n  type_name: \"Body\"\n}\n", res.valueRawSchema());
    }

    @Test()
    void testGetSchemasOfTopicInvalidBroker() {
        final var e = assertThrowsExactly(ToolCallException.class, () -> mcpTools.getSchemasOfTopic("unknown_broker", "InputProtobufTopic"));
        assertEquals("Unknown Broker identifier `unknown_broker`. Use `get_brokers` to get the list of available Brokers.", e.getMessage());
    }

    @Test()
    void testGetSchemasOfInvalidTopic() {
        final var e = assertThrowsExactly(ToolCallException.class, () -> mcpTools.getSchemasOfTopic("pi_broker", "UnavailableTopic"));
        assertEquals("Unknown Topic name `UnavailableTopic`. Use `get_topics_from_broker` to get the list of Kafka Topics for a parent Broker.", e.getMessage());
    }

    @Test
    void testGetTopicContentSample() {
        var res = mcpTools.getTopicContentSample("pi_broker", "InputTopic");
        assertEquals("InputTopic", res.topicName());
        assertEquals("pi_broker", res.brokerId());
        assertEquals("STRING", res.keySerde());
        assertEquals("AVRO", res.valueSerde());
        assertNotNull(res.value());
        assertNotNull(res.headers());
    }

    @Test()
    void testGetTopicContentSampleInvalidBroker() {
        final var e = assertThrowsExactly(ToolCallException.class, () -> mcpTools.getTopicContentSample("unknown_broker", "InputTopic"));
        assertEquals("Unknown Broker identifier `unknown_broker`. Use `get_brokers` to get the list of available Brokers.", e.getMessage());
    }

    @Test()
    void testGetTopicContentSampleInvalidTopic() {
        final var e = assertThrowsExactly(ToolCallException.class, () -> mcpTools.getTopicContentSample("pi_broker", "UnavailableTopic"));
        assertEquals("Unknown Topic name `UnavailableTopic`. Use `get_topics_from_broker` to get the list of Kafka Topics for a parent Broker.", e.getMessage());
    }
}
