# Log Functions

## debug

Logs the concatenation of evaluated expression(s) as DEBUG output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `debug(2+3)` → `5`

## error

Logs the concatenation of evaluated expression(s) as ERROR output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `error("Failed")` → `Failed`

## info

Logs the concatenation of evaluated expression(s) as INFO output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `info("r=", 2*3)` → `r=6`

## trace

Logs the concatenation of evaluated expression(s) as TRACE output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `trace("A", "b")` → `Ab`

## warn

Logs the concatenation of evaluated expression(s) as WARN output.

| Parameter | Description        |
|-----------|--------------------|
| `...`     | Expressions to log |

**Example:** `warn()` → (empty)
