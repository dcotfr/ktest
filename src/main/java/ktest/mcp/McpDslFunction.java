package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.smallrye.common.constraint.NotNull;

record McpDslFunction(@NotNull @JsonPropertyDescription("Name of the DSL Function") String functionName,
                      @NotNull @JsonPropertyDescription("Category of the DSL Function") String category,
                      @NotNull @JsonPropertyDescription("Example of parameters for the DSL Function") String parameters,
                      @NotNull @JsonPropertyDescription("Example of output of the DSL Function") String result,
                      @NotNull @JsonPropertyDescription("Description of the DSL Function") String description) {
}
