package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class McpTopics {
    @NotNull
    @JsonPropertyDescription("List of Kafka Topics associated with their parent Broker")
    public final List<McpTopic> topics = new ArrayList<>();
}
