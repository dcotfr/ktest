# Log Functions

## debug

Logs the concatenation of evaluated expression(s) as DEBUG output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `debug(2+3)` → `5`

Not available in MCP mode.

## error

Logs the concatenation of evaluated expression(s) as ERROR output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `error("Failed")` → `Failed`

Not available in MCP mode.

## info

Logs the concatenation of evaluated expression(s) as INFO output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `info("r=", 2*3)` → `r=6`

Not available in MCP mode.

## trace

Logs the concatenation of evaluated expression(s) as TRACE output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `trace("A", "b")` → `Ab`

Not available in MCP mode.

## warn

Logs the concatenation of evaluated expression(s) as WARN output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `warn()` → (empty)

Not available in MCP mode.
