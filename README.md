# ktest - Kafka testing utility [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dcotfr_ktest&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dcotfr_ktest)

Utility for defining and executing Kafka message test cases by sending, checking presence or absence of messages in topics with dynamic script-based content.

## Quick Start

1. **Install:** Download `ktest` Linux binary
2. **Configure:** Create `ktconfig.yml` → [see config guide](doc/03-config-file.md)
3. **Write tests:** Create `ktestcase.yml` → [see test case guide](doc/04-test-case-file.md)
4. **Run:** `ktest srun -e dev` → [see commands](doc/02-commands.md)

## Documentation

- [User Guide](doc/01-getting-started.md) - Complete user documentation
- [Scripting Reference](doc/05-scripting/00-overview.md) - All scripting functions
- [Advanced Topics](doc/06-advanced-topics.md) - FAQ and advanced usage
- [Examples](doc/07-examples.md) - Complete usage examples

## Commands

| Command | Description                               |
|---------|-------------------------------------------|
| `srun`  | Sequential run of test case(s)            |
| `prun`  | Parallel run of test case(s)              |
| `doc`   | Display full documentation                |
| `eval`  | Evaluates a script                        |
| `scan`  | Scan topic(s) to extract sample test case |

## License

[LICENSE](LICENSE)