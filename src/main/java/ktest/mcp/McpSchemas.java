package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;

record McpSchemas(@NotNull @JsonPropertyDescription("Name of the Kafka Topic") String topicName,
                  @NotNull @JsonPropertyDescription("Identifier of the parent Kafka Broker") String brokerId,
                  @JsonPropertyDescription("Type of Schema used for the Key") String keySchemaType,
                  @JsonPropertyDescription("Raw Schema definition used for the Key") String keyRawSchema,
                  @JsonPropertyDescription("Type of Schema used for the Value") String valueSchemaType,
                  @JsonPropertyDescription("Raw Schema definition used for the Value") String valueRawSchema) {
}
