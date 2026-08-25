package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;

record McpBroker(@NotNull @JsonPropertyDescription("Identifier of the Kafka Broker") String brokerId,
                 @Nullable @JsonPropertyDescription("Description of the Kafka Broker") String description) {
}
