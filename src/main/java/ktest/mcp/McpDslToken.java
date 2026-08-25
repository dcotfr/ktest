package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;

record McpDslToken(@NotNull @JsonPropertyDescription("Token of the DSL") String tokenName,
                   @NotNull @JsonPropertyDescription("Example of usage of the Token") String usage,
                   @NotNull @JsonPropertyDescription("Example of result of the Token") String result,
                   @NotNull @JsonPropertyDescription("Description of the DSL Token") String description) {
}
