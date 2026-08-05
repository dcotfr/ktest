# Test Case File

Test cases are described using YAML files. Multiple test cases can be in the same file, separated by `---`.

## Structure

| Field       | Description                                 |
|-------------|---------------------------------------------|
| `name`      | Test case name *(required)*                 |
| `tags`      | (Optional) Tags for filtering               |
| `beforeAll` | (Optional) Script executed before all steps |
| `afterAll`  | (Optional) Script executed after all steps  |
| `steps`     | List of steps *(required)*                  |

## Step Structure

| Field         | Description                                      |
|---------------|--------------------------------------------------|
| `name`        | Step name                                        |
| `before`      | (Optional) Script executed before this step      |
| `after`       | (Optional) Script executed after this step       |
| `broker`      | Broker name from config                          |
| `topic`       | Topic name                                       |
| `keySerde`    | (Optional) Key serde: `STRING`, `AVRO`, `JSON`   |
| `valueSerde`  | (Optional) Value serde: `STRING`, `AVRO`, `JSON` |
| `valueSchema` | (Optional) Forced schema name                    |
| `keySchema`   | (Optional) Forced key schema name                |
| `action`      | Action: `SEND`, `PRESENT`, `ABSENT`, `TODO`      |
| `record`      | Record definition                                |

## Record Structure

| Field     | Description                                      |
|-----------|--------------------------------------------------|
| `headers` | (Optional) Message headers (scripting supported) |
| `key`     | (Optional) Message key (scripting supported)     |
| `value`   | (Optional) Message value (scripting supported)   |

## Actions

| Action    | Description                     |
|-----------|---------------------------------|
| `SEND`    | Send a message to the topic     |
| `PRESENT` | Check for message presence      |
| `ABSENT`  | Check for message absence       |
| `TODO`    | Mark as in-progress (no action) |

## Goto Flow Control

Use `goto("StepName")` in scripts to jump to another step.

## Complete Example

```yaml
name: "Name of the Test Case"
tags: [ pi4, ... ]
beforeAll: |
  TIMESTAMP = now()
  ...
steps:
  - name: "Name of Step 1"
    before: RANDOM_UUID = uuid()
    broker: ktconfig_broker_name
    topic: DestinationTopicName
    keySerde: STRING
    valueSerde: AVRO
    valueSchema: forced-schema-name
    action: SEND
    record:
      headers:
        headAttribute1: "Sample record header value"
      key: |
        code: ${concat("UUID=", RANDOM_UUID)}
        label: Product 1
      value: |
        attribute1: SingleWord
        attribute2: 2.0
    after: pause(100)
  - name: "Name of Step 2"
    ...
afterAll: info("All steps are finished.")
---
name: NameOfSecondTestCase
...
```

## Related Documentation

- [Commands Reference](02-commands.md) - How to run test cases (`-f` option)
- [Scripting Reference](05-scripting/00-overview.md) - Operators and functions
- [Advanced Topics](06-advanced-topics.md) - FAQ and advanced usage