# Complete Examples

## Example 1: Goto Loop for Batch Sending

This example demonstrates how to send multiple events using a goto loop:

```yaml
name: Goto Test Case
beforeAll: |
  LOOP_COUNT = 10
steps:
  - name: Send 10 events
    broker: ktconfig_broker_name
    topic: DestinationTopicName
    valueSerde: AVRO
    action: SEND
    record:
      key: "Loop-${LOOP_COUNT}"
      value: |
        attribute1: "Sample value"
        attribute2: ${LOOP_COUNT}
    after: |
      LOOP_COUNT = LOOP_COUNT - 1
      LOOP_COUNT!=0 ? goto("Send 10 events")
  - name: Verify result
    broker: ktconfig_broker_name
    topic: DestinationTopicName
    action: PRESENT
    record:
      key: |
        attribute1: "Sample value"
```

## Example 2: Custom Schema Testing

This example demonstrates how to test a topic with a custom schema naming strategy:

```yaml
name: Custom Subject Naming Strategy
steps:
  - name: Forced Value Schema
    broker: ktconfig_broker_name
    topic: DestinationTopicName
    valueSerde: AVRO
    valueSchema: forced-schema-name
    action: SEND
    record:
      key: "test-key"
      value: |
        field1: "sample data"
        field2: 42
  - name: Verify message content
    broker: ktconfig_broker_name
    topic: DestinationTopicName
    action: PRESENT
    record:
      key: |
        field1: "sample data"
```

## Example 3: Tags Filter for Selective Execution

This example shows how to use tags to filter test case execution:

```yaml
name: Pi4 Integration Test
tags: [ pi4, integration ]
steps:
  - name: Send test message
    broker: pi_broker
    topic: test-topic
    action: SEND
    record:
      key: "test"
      value: |
        data: "pi4 payload"
  - name: Verify message
    broker: pi_broker
    topic: test-topic
    action: PRESENT
    record:
      key: "test"

---
name: Pi3 Compatibility Test
tags: [ pi3, compatibility ]
steps:
  - name: Send legacy message
    broker: pi_broker
    topic: legacy-topic
    action: SEND
    record:
      key: "legacy"
      value: |
        data: "pi3 payload"
  - name: Verify legacy message
    broker: pi_broker
    topic: legacy-topic
    action: PRESENT
    record:
      key: "legacy"

---
name: Multi-Environment Test
tags: [ pi3, pi4, integration ]
steps:
  - name: Send cross-env message
    broker: pi_broker
    topic: cross-env-topic
    action: SEND
    record:
      key: "cross"
      value: |
        data: "multi-env payload"
```

**Execution examples:**
- `ktest prun ... -t pi4` → runs only "Pi4 Integration Test"
- `ktest srun ... --tags "pi3+integration"` → runs only "Multi-Environment Test"
- `ktest prun ... -t "pi4,pi3"` → runs "Pi4 Integration Test" AND "Pi3 Compatibility Test"