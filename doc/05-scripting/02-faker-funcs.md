# Faker Functions

## regexgen

Generates a random string matching the provided regex pattern.

| Parameter | Description            |
|-----------|------------------------|
| `pattern` | Regex pattern to match |

**Example:** `regexgen("E-[A-Z]{2,4}#{2}")` → `"E-AJD##"`

## uuid

Generates a random UUID.

**Example:** `uuid()` → `"fd48147a-58ba-461b-b71c-f44c89ba67ca"`
