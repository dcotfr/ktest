package ktest.mcp;

import io.quarkiverse.mcp.server.test.McpAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;

import static org.awaitility.Awaitility.setDefaultTimeout;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class McpServerIT {
    private static final Logger LOG = LoggerFactory.getLogger(McpServerIT.class);

    @BeforeAll
    static void beforeAll() {
        setDefaultTimeout(Duration.ofSeconds(30));
    }

    @Test
    void testToolsList() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsList(res -> {
                        LOG.info("Tools response: {}", res);
                        assertEquals(6, res.size());

                        var toolInfo = res.findByName("get_environments");
                        assertEquals("Get Test Environments", toolInfo.title());
                        assertEquals("Use this function to get the list of Test Environments known by `ktest`", toolInfo.description());
                        assertEquals("{\"type\":\"object\",\"properties\":{},\"required\":[]}", toolInfo.inputSchema().toString());
                        assertEquals("{\"type\":\"object\",\"properties\":{\"environments\":{\"description\":\"List of Test Environments known by `ktest`\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"description\":{\"type\":\"string\",\"description\":\"Description of the Test Environment\"},\"envId\":{\"type\":\"string\",\"description\":\"Identifier of the Test Environment\"}}}}}}", toolInfo.outputSchema().toString());
                        assertAnnotations(toolInfo, true, true, false, false);

                        toolInfo = res.findByName("get_brokers");
                        assertEquals("Get Kafka Brokers", toolInfo.title());
                        assertEquals("Use this function to get the list of Kafka Brokers known by `ktest`", toolInfo.description());
                        assertEquals("{\"type\":\"object\",\"properties\":{},\"required\":[]}", toolInfo.inputSchema().toString());
                        assertEquals("{\"type\":\"object\",\"properties\":{\"brokers\":{\"description\":\"List of Kafka Brokers known by `ktest`\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"brokerId\":{\"type\":\"string\",\"description\":\"Identifier of the Kafka Broker\"},\"description\":{\"type\":\"string\",\"description\":\"Description of the Kafka Broker\"}}}}}}", toolInfo.outputSchema().toString());
                        assertAnnotations(toolInfo, true, true, false, false);

                        toolInfo = res.findByName("get_dsl_documentation");
                        assertEquals("Get DSL documentation", toolInfo.title());
                        assertEquals("Use this function to get the documentation of the internal Domain-Specific Language of `ktest`", toolInfo.description());
                        assertAnnotations(toolInfo, true, true, false, false);

                        toolInfo = res.findByName("get_topics_from_broker");
                        assertEquals("Get Kafka Topics from Broker", toolInfo.title());
                        assertEquals("Use this function to get the list of Kafka Topics for a parent Broker", toolInfo.description());
                        assertEquals("{\"type\":\"object\",\"properties\":{\"brokerId\":{\"type\":\"string\",\"description\":\"Identifier of the parent Kafka Broker\"}},\"required\":[\"brokerId\"]}", toolInfo.inputSchema().toString());
                        assertEquals("{\"type\":\"object\",\"properties\":{\"topics\":{\"description\":\"List of Kafka Topics associated with their parent Broker\",\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"brokerId\":{\"type\":\"string\",\"description\":\"Identifier of the parent Kafka Broker\"},\"topicName\":{\"type\":\"string\",\"description\":\"Name of the Kafka Topic\"}}}}}}", toolInfo.outputSchema().toString());
                        assertAnnotations(toolInfo, true, true, true, false);

                        toolInfo = res.findByName("get_schemas_of_topic");
                        assertEquals("Get Schemas of a Topic", toolInfo.title());
                        assertEquals("Use this function to get the Schemas (Avro, Json, Protobuf) defined for the Key and Value of a Topic", toolInfo.description());
                        assertEquals("{\"type\":\"object\",\"properties\":{\"brokerId\":{\"type\":\"string\",\"description\":\"Identifier of the parent Kafka Broker\"},\"topicName\":{\"type\":\"string\",\"description\":\"Name of the Kafka Topic\"}},\"required\":[\"brokerId\",\"topicName\"]}", toolInfo.inputSchema().toString());
                        assertEquals("{\"type\":\"object\",\"properties\":{\"brokerId\":{\"type\":\"string\",\"description\":\"Identifier of the parent Kafka Broker\"},\"keyRawSchema\":{\"type\":\"string\",\"description\":\"Raw Schema definition used for the Key\"},\"keySchemaType\":{\"type\":\"string\",\"description\":\"Type of Schema used for the Key\"},\"topicName\":{\"type\":\"string\",\"description\":\"Name of the Kafka Topic\"},\"valueRawSchema\":{\"type\":\"string\",\"description\":\"Raw Schema definition used for the Value\"},\"valueSchemaType\":{\"type\":\"string\",\"description\":\"Type of Schema used for the Value\"}}}", toolInfo.outputSchema().toString());
                        assertAnnotations(toolInfo, true, true, true, false);

                        toolInfo = res.findByName("get_topic_content_sample");
                        assertEquals("Get Topic Content Sample", toolInfo.title());
                        assertEquals("Use this function to get a sample of the latest record from a Kafka Topic (headers, key, value as generic JSON)", toolInfo.description());
                        assertEquals("{\"type\":\"object\",\"properties\":{\"brokerId\":{\"type\":\"string\",\"description\":\"Identifier of the parent Kafka Broker\"},\"topicName\":{\"type\":\"string\",\"description\":\"Name of the Kafka Topic\"}},\"required\":[\"brokerId\",\"topicName\"]}", toolInfo.inputSchema().toString());
                        assertAnnotations(toolInfo, true, true, true, false);
                    })
                    .thenAssertResults();
        }
    }

    @Test
    void testGetEnvironments() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_environments", Map.of(),
                            res -> {
                                LOG.info("Environments response: {}", res);
                                assertEquals("{\"environments\":[{\"envId\":\"pi\",\"description\":\"Default environment for `pi_broker` broker\"},{\"envId\":\"piTag\",\"description\":\"Environment targeting `pi_broker` broker and filtering tests by tag1 & tag3\"},{\"envId\":\"local\",\"description\":\"Default environment for `local_broker` broker\"}]}", res.structuredContent().toString());
                            }
                    )
                    .thenAssertResults();
        }
    }

    @Test
    void testGetBrokers() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_brokers", Map.of(),
                            res -> {
                                LOG.info("Brokers response: {}", res);
                                assertEquals("{\"brokers\":[{\"brokerId\":\"pi_broker\",\"description\":\"Integration test broker on Pi\"},{\"brokerId\":\"local_broker\",\"description\":\"Local broker\"}]}", res.structuredContent().toString());
                            }
                    )
                    .thenAssertResults();
        }
    }

    @Test
    void testGetTopicsFromBroker() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_topics_from_broker", Map.of("brokerId", "pi_broker"),
                            res -> {
                                LOG.info("Topics response: {}", res);
                                assertEquals("{\"topics\":[{\"topicName\":\"CompactTopic\",\"brokerId\":\"pi_broker\"},{\"topicName\":\"InputJsonTopic\",\"brokerId\":\"pi_broker\"},{\"topicName\":\"InputProtobufTopic\",\"brokerId\":\"pi_broker\"},{\"topicName\":\"InputTopic\",\"brokerId\":\"pi_broker\"},{\"topicName\":\"InputTopicStr\",\"brokerId\":\"pi_broker\"},{\"topicName\":\"OutputTopic\",\"brokerId\":\"pi_broker\"},{\"topicName\":\"OutputTopicStr\",\"brokerId\":\"pi_broker\"}]}", res.structuredContent().toString());
                            }
                    )
                    .thenAssertResults();
        }
    }

    @Test
    void testGetTopicsFromInvalidBroker() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_topics_from_broker", Map.of("brokerId", "fake_broker"),
                            res -> {
                                LOG.info("Invalid Topics response: {}", res);
                                assertTrue(res.isError());
                                assertEquals("Unknown Broker identifier `fake_broker`. Use `get_brokers` to get the list of available Brokers.", res.firstContent().asText().text());
                            })
                    .thenAssertResults();
        }
    }

    @Test
    void testGetSchemasOfTopic() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_schemas_of_topic", Map.of("brokerId", "pi_broker", "topicName", "InputTopic"),
                            res -> {
                                LOG.info("Schemas response: {}", res);
                                assertEquals("{\"topicName\":\"InputTopic\",\"brokerId\":\"pi_broker\",\"keySchemaType\":null,\"keyRawSchema\":null,\"valueSchemaType\":\"AVRO\",\"valueRawSchema\":\"{\\\"type\\\":\\\"record\\\",\\\"name\\\":\\\"InputTopicValue\\\",\\\"namespace\\\":\\\"ktest\\\",\\\"fields\\\":[{\\\"name\\\":\\\"sender\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"eventType\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"eventTsp\\\",\\\"type\\\":{\\\"type\\\":\\\"long\\\",\\\"logicalType\\\":\\\"timestamp-millis\\\"}},{\\\"name\\\":\\\"body\\\",\\\"type\\\":{\\\"type\\\":\\\"record\\\",\\\"name\\\":\\\"Body\\\",\\\"fields\\\":[{\\\"name\\\":\\\"code\\\",\\\"type\\\":[\\\"null\\\",\\\"string\\\"],\\\"default\\\":null},{\\\"name\\\":\\\"label\\\",\\\"type\\\":[\\\"null\\\",\\\"string\\\"],\\\"default\\\":null},{\\\"name\\\":\\\"commandAt\\\",\\\"type\\\":[\\\"null\\\",{\\\"type\\\":\\\"long\\\",\\\"logicalType\\\":\\\"timestamp-millis\\\"}],\\\"default\\\":null},{\\\"name\\\":\\\"sentAt\\\",\\\"type\\\":[\\\"null\\\",{\\\"type\\\":\\\"long\\\",\\\"logicalType\\\":\\\"timestamp-millis\\\"}],\\\"default\\\":null},{\\\"name\\\":\\\"weight\\\",\\\"type\\\":[\\\"null\\\",\\\"double\\\"],\\\"default\\\":null}]}}]}\"}", res.structuredContent().toString());
                            }
                    )
                    .thenAssertResults();
        }
    }

    @Test
    void testGetSchemasOfUnknownTopic() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_schemas_of_topic", Map.of("brokerId", "pi_broker", "topicName", "InvalidTopic"),
                            res -> {
                                LOG.info("Invalid Topics response: {}", res);
                                assertTrue(res.isError());
                                assertEquals("Unknown Topic name `InvalidTopic`. Use `get_topics_from_broker` to get the list of Kafka Topics for a parent Broker.", res.firstContent().asText().text());
                            }
                    )
                    .thenAssertResults();
        }
    }

    @Test
    void testGetTopicContentSample() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_topic_content_sample", Map.of("brokerId", "pi_broker", "topicName", "InputTopic"),
                            res -> {
                                LOG.info("Content sample response: {}", res);
                                assertFalse(res.isError());
                                final var content = res.structuredContent();
                                assertNotNull(content);
                                assertTrue(content.toString().contains("\"topicName\":\"InputTopic\""));
                                assertTrue(content.toString().contains("\"brokerId\":\"pi_broker\""));
                                assertTrue(content.toString().contains("\"valueSerde\":\"AVRO\""));
                            }
                    )
                    .thenAssertResults();
        }
    }

    @Test
    void testGetTopicContentSampleInvalidBroker() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_topic_content_sample", Map.of("brokerId", "fake_broker", "topicName", "InputTopic"),
                            res -> {
                                LOG.info("Invalid broker response: {}", res);
                                assertTrue(res.isError());
                                assertEquals("Unknown Broker identifier `fake_broker`. Use `get_brokers` to get the list of available Brokers.", res.firstContent().asText().text());
                            }
                    )
                    .thenAssertResults();
        }
    }

    @Test
    void testGetTopicContentSampleInvalidTopic() {
        try (final var client = initMcpTestClient()) {
            client.when()
                    .toolsCall("get_topic_content_sample", Map.of("brokerId", "pi_broker", "topicName", "InvalidTopic"),
                            res -> {
                                LOG.info("Invalid topic response: {}", res);
                                assertTrue(res.isError());
                                assertEquals("Unknown Topic name `InvalidTopic`. Use `get_topics_from_broker` to get the list of Kafka Topics for a parent Broker.", res.firstContent().asText().text());
                            }
                    )
                    .thenAssertResults();
        }
    }

    private static McpAssured.McpStdioTestClient initMcpTestClient() {
        return McpAssured.newStdioClient().setCommand("target/ktest-runner", "mcp").build().connect();
    }

    private static void assertAnnotations(final McpAssured.ToolInfo pToolInfo,
                                          final boolean pReadOnly, final boolean pIdempotent, final boolean pOpenWorld, final boolean pDestructive) {
        final var annotations = pToolInfo.annotations().get();
        assertEquals(pReadOnly, annotations.readOnlyHint());
        assertEquals(pIdempotent, annotations.idempotentHint());
        assertEquals(pOpenWorld, annotations.openWorldHint());
        assertEquals(pDestructive, annotations.destructiveHint());
    }
}
