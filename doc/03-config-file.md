# Configuration File

The YAML configuration file is called by default `ktconfig.yml` in the same directory as the executable.

## Structure

The configuration file contains three main sections:

- **registries**: Schema registry connections
- **brokers**: Kafka broker connections
- **environments**: Environment-specific settings

## Registries

Schema registries used for Avro schema resolution.

| Field      | Description                            |
|------------|----------------------------------------|
| `name`     | Registry identifier name               |
| `url`      | Registry HTTP URL                      |
| `user`     | (Optional) Username for authentication |
| `password` | (Optional) Password for authentication |

## Brokers

Kafka broker configurations.

| Field               | Description                                   |
|---------------------|-----------------------------------------------|
| `name`              | Broker identifier name                        |
| `description`       | Description of the Kafka Broker (used by MCP) |
| `bootstrap.servers` | Kafka bootstrap servers address               |
| `registry`          | (Optional) Registry name reference            |
| `sasl.jaas.config`  | (Optional) SASL JAAS configuration            |
| `sasl.mechanism`    | (Optional) SASL mechanism                     |
| `security.protocol` | (Optional) Security protocol                  |
| `group.id`          | (Optional) Consumer group ID                  |
| `send.timeout.sec`  | (Optional) Send timeout in seconds            |
| `poll.duration.ms`  | (Optional) Poll duration in milliseconds      |

## Environments

Environment-specific settings and scripts.

| Field         | Description                                       |
|---------------|---------------------------------------------------|
| `name`        | Test Environment name (used with `-e` option)     |
| `description` | Description of the Test Environment (used by MCP) |
| `options`     | (Optional) Preset options                         |
| `onStart`     | (Optional) Script executed before all test cases  |
| `onEnd`       | (Optional) Script executed after all test cases   |

### Preset Options

Options that can be preset per environment:

| Option       | Description                               |
|--------------|-------------------------------------------|
| `backOffset` | Back offset for message scanning          |
| `matrix`     | Matrix summary file path (`-` to disable) |
| `report`     | Test report file path (`-` to disable)    |
| `tags`       | Tags to filter test cases                 |

## Complete Example

```yaml
registries:
  - name: pi_registry
    url: http://192.168.0.105:8081
    user: UserName
    password: UserPassword
  - name: registry_2
    ...

brokers:
  - name: pi_broker
    description: Integration Kafka broker deployed on Pi
    bootstrap.servers: 192.168.0.105:9092
    registry: pi_registry
    sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username='USER' password='${env("PASSWORD")}';
    sasl.mechanism: PLAIN
    security.protocol: SASL_SSL
    group.id: pi.ktest-group
    send.timeout.sec: 30
    poll.duration.ms: 5000
  - name: local_broker
    ...

environments:
  - name: pi
    description: Default integration test environment using `pi_broker` broker
    options:
      backOffset: 100
      matrix: excel.xlsx
      report: junit.xml
      tags: pi3+4g,pi4
    onStart: |
      BROKER_USED="pi_broker"
      ...
    onEnd: info("Test finished")
  - name: dev
    ...
```

## Related Documentation

- [Commands Reference](02-commands.md) - How to use `-c` and `-e` options
- [Test Case File](04-test-case-file.md) - Test case configuration