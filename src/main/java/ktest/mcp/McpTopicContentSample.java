package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;

import java.util.Map;

record McpTopicContentSample(@NotNull @JsonPropertyDescription("Name of the Kafka Topic") String topicName,
                             @NotNull @JsonPropertyDescription("Identifier of the parent Kafka Broker") String brokerId,
                             @JsonPropertyDescription("Serde used for the Key") String keySerde,
                             @JsonPropertyDescription("Serde used for the Value") String valueSerde,
                             @JsonPropertyDescription("Message headers") Map<String, String> headers,
                             @JsonPropertyDescription("Key content as generic JSON") String key,
                             @JsonPropertyDescription("Value content as generic JSON") String value) {
}
