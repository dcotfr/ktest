package ktest.mcp;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.ArrayList;
import java.util.List;

public final class McpDslDocumentation {
    @JsonPropertyDescription("DSL usage Rules")
    public final String rules = "When a DSL script must be written, ALWAYS verify that ALL functions an tokens used are properly defined and documented in this documentation.";

    @JsonPropertyDescription("List of Tokens available in the DSL of `ktest`")
    public final List<McpDslToken> tokens = new ArrayList<>();

    @JsonPropertyDescription("List of scripting Functions available in the DSL of `ktest`")
    public final List<McpDslFunction> functions = new ArrayList<>();
}
