package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;

record McpTopic(@NotNull @JsonPropertyDescription("Name of the Kafka Topic") String topicName,
                @NotNull @JsonPropertyDescription("Identifier of the parent Kafka Broker") String brokerId) {
}
