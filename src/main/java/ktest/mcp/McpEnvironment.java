package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.common.constraint.Nullable;

record McpEnvironment(@NotNull @JsonPropertyDescription("Identifier of the Test Environment") String envId,
                      @Nullable @JsonPropertyDescription("Description of the Test Environment") String description) {
}
