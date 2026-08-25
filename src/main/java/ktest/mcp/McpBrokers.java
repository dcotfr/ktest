package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.ArrayList;
import java.util.List;

public final class McpBrokers {
    @JsonPropertyDescription("List of Kafka Brokers known by `ktest`")
    public final List<McpBroker> brokers = new ArrayList<>();
}
