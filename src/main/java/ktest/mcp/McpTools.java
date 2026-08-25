package ktest.mcp;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import ktest.domain.TestRecord;
import ktest.domain.config.KTestConfig;
import ktest.kafka.ClusterClient;
import ktest.kafka.SchemaWrapper;
import ktest.scan.ScanService;
import ktest.script.Context;

import java.util.Comparator;

public final class McpTools {
    private final Instance<KTestConfig> configFactory;
    private final ClusterClient clusterClient;
    private final ScanService scanService;
    private final Context context;

    @Inject
    McpTools(final Instance<KTestConfig> pConfigFactory, final ClusterClient pClusterClient, final ScanService pScanService, final Context pContext) {
        configFactory = pConfigFactory;
        clusterClient = pClusterClient;
        scanService = pScanService;
        context = pContext;
    }

    @Tool(name = "get_environments", title = "Get Test Environments",
            description = "Use this function to get the list of Test Environments known by `ktest`",
            structuredContent = true,
            annotations = @Tool.Annotations(readOnlyHint = true, idempotentHint = true, openWorldHint = false, destructiveHint = false))
    public McpEnvironments getEnvironments() {
        final var res = new McpEnvironments();
        for (final var env : configFactory.get().environments()) {
            res.environments.add(new McpEnvironment(env.name(), env.description()));
        }
        return res;
    }

    @Tool(name = "get_brokers", title = "Get Kafka Brokers",
            description = "Use this function to get the list of Kafka Brokers known by `ktest`",
            structuredContent = true,
            annotations = @Tool.Annotations(readOnlyHint = true, idempotentHint = true, openWorldHint = false, destructiveHint = false))
    public McpBrokers getBrokers() {
        final var res = new McpBrokers();
        for (final var broker : configFactory.get().brokers()) {
            res.brokers.add(new McpBroker(broker.name(), broker.description()));
        }
        return res;
    }

    @Tool(name = "get_dsl_documentation", title = "Get DSL documentation",
            description = "Use this function to get the documentation of the internal Domain-Specific Language of `ktest`",
            structuredContent = true,
            annotations = @Tool.Annotations(readOnlyHint = true, idempotentHint = true, openWorldHint = false, destructiveHint = false))
    public McpDslDocumentation getDslDocumentation() {
        final var res = new McpDslDocumentation();
        res.tokens.add(new McpDslToken("-", "-3", "-3", "Unary minus Operator: negates the number value"));
        res.tokens.add(new McpDslToken("+", "4+3", "7", "Addition Operator: adds two numbers."));
        res.tokens.add(new McpDslToken("-", "9-5", "4", "Subtraction Operator; subtracts second number from first number."));
        res.tokens.add(new McpDslToken("*", "2*3", "6", "Multiplication Operator: multiplies two numbers."));
        res.tokens.add(new McpDslToken("/", "5/2", "2.5", "Division Operator: divides first number by second number."));
        res.tokens.add(new McpDslToken("=", "A=3.14", "", "Assignment Operator."));
        res.tokens.add(new McpDslToken("(", "3*(1+2)", "9", "Left brace: start increased priority."));
        res.tokens.add(new McpDslToken(")", "-(-4)", "4", "Right brace: ends increased priority."));
        res.tokens.add(new McpDslToken("?", "cnd?stm", "", "Executes a statement only if condition is true (=1)."));
        res.tokens.add(new McpDslToken("?:", "c?true:else", "", "Ternary if: execute 'true' statement if condition is true, else execute 'else' statement."));
        res.tokens.add(new McpDslToken("==", "\"A\"==\"A\"", "1", "Equal: true if arguments are equal."));
        res.tokens.add(new McpDslToken("!=", "5!=5", "0", "Not Equal: true if arguments are different."));
        res.tokens.add(new McpDslToken("<=", "2<=1+1", "1", "Lesser or Equal: true if left argument is smaller or equal to right argument."));
        res.tokens.add(new McpDslToken("<", "2<2", "0", "Lesser: true if left argument is strictly smaller than right argument."));
        res.tokens.add(new McpDslToken(">=", "0>=1", "0", "Greater or Equal: true if left argument is greater or equal to right argument."));
        res.tokens.add(new McpDslToken(">", "3>2", "1", "Greater: true if left argument is strictly greater than right argument."));
        res.tokens.add(new McpDslToken(";", "", "", "Ends the current in-line statement and starts a new one."));
        for (final var f : context.functions().stream()
                .filter(x -> x.doc().mcpDescription() != null)
                .sorted(Comparator.comparing(o -> o.doc().type())).toList()) {
            final var doc = f.doc();
            res.functions.add(new McpDslFunction(f.command(), doc.type().name(), doc.param(), doc.result(), doc.mcpDescription()));
        }
        return res;
    }

    @Tool(name = "get_topics_from_broker", title = "Get Kafka Topics from Broker",
            description = "Use this function to get the list of Kafka Topics for a parent Broker",
            structuredContent = true,
            annotations = @Tool.Annotations(readOnlyHint = true, idempotentHint = true, destructiveHint = false))
    public McpTopics getTopicsFromBroker(@ToolArg(name = "brokerId", description = "Identifier of the parent Kafka Broker") String pBrokerId) {
        validateBrokerId(pBrokerId);

        final var res = new McpTopics();
        for (final var topic : scanService.listUserTopics("", pBrokerId)) {
            res.topics.add(new McpTopic(topic.topic(), topic.broker()));
        }
        return res;
    }

    @Tool(name = "get_schemas_of_topic", title = "Get Schemas of a Topic",
            description = "Use this function to get the Schemas (Avro, Json, Protobuf) defined for the Key and Value of a Topic",
            structuredContent = true,
            annotations = @Tool.Annotations(readOnlyHint = true, idempotentHint = true, destructiveHint = false))
    public McpSchemas getSchemasOfTopic(@ToolArg(name = "brokerId", description = "Identifier of the parent Kafka Broker") String pBrokerId,
                                        @ToolArg(name = "topicName", description = "Name of the Kafka Topic") String pTopicName) {
        validateBrokerIdTopicName(pBrokerId, pTopicName);

        final var topicSchemas = scanService.lastActiveSchemas("", pBrokerId, pTopicName);
        final var keySchema = topicSchemas.keySchema();
        final var valueSchema = topicSchemas.valueSchema();
        return new McpSchemas(pTopicName, pBrokerId,
                toSchemaType(keySchema), toRawSchema(keySchema),
                toSchemaType(valueSchema), toRawSchema(valueSchema));
    }

    @Tool(name = "get_topic_content_sample", title = "Get Topic Content Sample",
            description = "Use this function to get a sample of the latest record from a Kafka Topic (headers, key, value as generic JSON)",
            structuredContent = true,
            annotations = @Tool.Annotations(readOnlyHint = true, idempotentHint = true, destructiveHint = false))
    public McpTopicContentSample getTopicContentSample(@ToolArg(name = "brokerId", description = "Identifier of the parent Kafka Broker") String pBrokerId,
                                                       @ToolArg(name = "topicName", description = "Name of the Kafka Topic") String pTopicName) {
        validateBrokerIdTopicName(pBrokerId, pTopicName);

        final var topicRef = scanService.scanSerdes("", pBrokerId, pTopicName);
        final var found = clusterClient.find("", topicRef, new TestRecord(null, null, null, null), 1);
        if (found == null) {
            throw new ToolCallException("No records found in topic `" + pTopicName + "` on broker `" + pBrokerId + "`.");
        }

        return new McpTopicContentSample(pTopicName, pBrokerId, topicRef.keySerde().name(), topicRef.valueSerde().name(), found.headers(), found.key().toString(), found.value().toString());
    }

    private static String toSchemaType(final SchemaWrapper pSchemaWrapper) {
        if (pSchemaWrapper == null || pSchemaWrapper.serde().name() == null) {
            return null;
        }
        return pSchemaWrapper.serde().name();
    }

    private static String toRawSchema(final SchemaWrapper pSchemaWrapper) {
        if (pSchemaWrapper == null) {
            return null;
        }
        if (pSchemaWrapper instanceof SchemaWrapper.Avro avro) {
            return avro.schema().toString();
        } else if (pSchemaWrapper instanceof SchemaWrapper.Json json) {
            return json.schema().toString();
        } else if (pSchemaWrapper instanceof SchemaWrapper.Protobuf proto) {
            return proto.schema().toProto().toString();
        }
        return pSchemaWrapper.toString();
    }

    private void validateBrokerId(final String pBrokerId) {
        if (!configFactory.get().brokers().stream().anyMatch(b -> b.name().equals(pBrokerId))) {
            throw new ToolCallException("Unknown Broker identifier `" + pBrokerId + "`. Use `get_brokers` to get the list of available Brokers.");
        }
    }

    private void validateBrokerIdTopicName(final String pBrokerId, final String pTopicName) {
        validateBrokerId(pBrokerId);

        if (!scanService.topicExists(pBrokerId, pTopicName)) {
            throw new ToolCallException("Unknown Topic name `" + pTopicName + "`. Use `get_topics_from_broker` to get the list of Kafka Topics for a parent Broker.");
        }
    }
}
