package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class McpEnvironments {
    @NotNull
    @JsonPropertyDescription("List of Test Environments known by `ktest`")
    public final List<McpEnvironment> environments = new ArrayList<>();
}
