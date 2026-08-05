# Misc Functions

## coalesce

Returns the first defined and non empty value.

| Parameter | Description     |
|-----------|-----------------|
| `...`     | Values to check |

**Example:** `coalesce(a, "", 5.2)` → `5.2`

## env

Returns the value of an ENV variable (with optional default).

| Parameter | Description               |
|-----------|---------------------------|
| `varName` | Environment variable name |
| `default` | Optional default value    |

**Example:** `env("SHELL", "default")` → `/bin/bash`

## goto

Jump and continue to named Step.

| Parameter  | Description             |
|------------|-------------------------|
| `stepName` | Name of the target step |

**Example:** `goto("NameOfStep")` → (jumps to step)

## jq

Returns the value of an attribute from a json string.

| Parameter | Description          |
|-----------|----------------------|
| `json`    | JSON string          |
| `path`    | JSON path expression |

**Example:** `jq("{\"a\":{\"b\":3.4}}", "/a/b")` → `3.4`

## pause

Pause treatment during provided milliseconds.

| Parameter      | Description           |
|----------------|-----------------------|
| `milliseconds` | Delay in milliseconds |

**Example:** `pause(3000)` → (pauses 3 seconds)

## record

Returns a json description of the last record found.

| Parameter | Description |
|-----------|-------------|
| *(none)*  | —           |

**Example:** `record()` → `{"topic": "..."}`
