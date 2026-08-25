# Text Functions

## concat

Returns the concatenation of multiple strings.

| Parameter | Description            |
|-----------|------------------------|
| `...`     | Strings to concatenate |

**Example:** `concat("Aaa", "Bbb",...)` → `AaaBbb`

## left

Returns the x first characters of a string.

| Parameter | Description                        |
|-----------|------------------------------------|
| `text`    | Input string                       |
| `count`   | Number of characters from the left |

**Example:** `left("Sample", 3)` → `Sam`

## length

Returns the length of a string.

| Parameter | Description  |
|-----------|--------------|
| `text`    | Input string |

**Example:** `length("Short text")` → `10`

## lower

Returns the lower cased string.

| Parameter | Description  |
|-----------|--------------|
| `text`    | Input string |

**Example:** `lower("ToLower")` → `tolower`

## ltrim

Returns the string with all left spaces removed.

| Parameter | Description  |
|-----------|--------------|
| `text`    | Input string |

**Example:** `ltrim(" Test ")` → `Test `

## replace

Returns a new string with old substring replaced by new substring.

| Parameter | Description           |
|-----------|-----------------------|
| `text`    | Input string          |
| `old`     | Substring to find     |
| `new`     | Replacement substring |

**Example:** `replace("ABAB", "B", "a")` → `AaAa`

## right

Returns the x last characters of a string.

| Parameter | Description                         |
|-----------|-------------------------------------|
| `text`    | Input string                        |
| `count`   | Number of characters from the right |

**Example:** `right("Sample", 3)` → `ple`

## rtrim

Returns the string with all right spaces removed.

| Parameter | Description  |
|-----------|--------------|
| `text`    | Input string |

**Example:** `rtrim(" Test ")` → ` Test`

## upper

Returns the upper cased string.

| Parameter | Description  |
|-----------|--------------|
| `text`    | Input string |

**Example:** `upper("ToUpper")` → `TOUPPER`
