# Time Functions

## now

Returns the current time in millis.

| Parameter | Description |
|-----------|-------------|
| *(none)*  | —           |

**Example:** `now()` → `1708808432990`

## time2txt

Returns the formatted date/string of a timestamp at current TimeZone.

| Parameter   | Description                                       |
|-------------|---------------------------------------------------|
| `format`    | Date format pattern (e.g., `yyyy-MM-dd HH:mm:ss`) |
| `timestamp` | Timestamp in milliseconds                         |

**Example:** `time2txt("yyyy-MM-dd HH:mm:ss", 1708854821321)` → `2024-02-25 10:53:41`

## txt2time

Returns the timestamp of a formatted date string.

| Parameter | Description                              |
|-----------|------------------------------------------|
| `format`  | Date format pattern (e.g., `yyyy/MM/dd`) |
| `text`    | Date string to parse                     |

**Example:** `txt2time("yyyy/MM/dd", "2024/07/17")` → `1721174400000`
