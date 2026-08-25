# Commands Reference

## srun - Sequential Run

Sequential execution of test case(s).

**Usage:** `ktest srun [-hV] [-b=<backOffset>] [-c=<config>] -e=<env> [-f=<file>] [-m=<matrix>] [-r=<report>] [-t=<tags>]`

### Options

| Option                    | Description                                                                     | Default         |
|---------------------------|---------------------------------------------------------------------------------|-----------------|
| `-b, --back=<backOffset>` | Back offset                                                                     | `250`           |
| `-c, --config=<config>`   | Path of the config file                                                         | `ktconfig.yml`  |
| `-e, --env=<env>`         | Name of the environment to use                                                  | *(required)*    |
| `-f, --file=<file>`       | Path of test case description file to execute                                   | `ktestcase.yml` |
| `-m, --matrix=<matrix>`   | Path of matrix summary (xlsx format) (`-` to disable)                           | `ktmatrix.xlsx` |
| `-p, --pause=<autoPause>` | Delay of auto pause before first PRESENT/ABSENT following SEND (0 for no pause) | `0`             |
| `-r, --report=<report>`   | Path of test report (JUnit format) (`-` to disable)                             | `ktreport.xml`  |
| `-t, --tags=<tags>`       | Tags to filter test cases to run                                                |                 |
| `-h, --help`              | Show this help message and exit                                                 |                 |
| `-V, --version`           | Print version information and exit                                              |                 |

### Exit Status

| Status | Meaning                                            |
|--------|----------------------------------------------------|
| `0`    | All tests succeeded                                |
| `1`    | At least one test failed or there was an exception |
| `2`    | An unexpected exception occurs                     |

---

## prun - Parallel Run

Parallel execution of test case(s).

**Usage:** `ktest prun [-hV] [-b=<backOffset>] [-c=<config>] -e=<env> [-f=<file>] [-m=<matrix>] [-r=<report>] [-t=<tags>]`

Accepts the same options as the `srun` command. Exit status is the same as `srun`.

---

## doc - Display Documentation

Display the full documentation of scripting functions.

**Usage:** `ktest doc [-hV]`

### Options

| Option          | Description                        |
|-----------------|------------------------------------|
| `-h, --help`    | Show this help message and exit    |
| `-V, --version` | Print version information and exit |

---

## eval - Evaluate Script

Evaluates a script and displays its final result.

**Usage:** `ktest eval [-hV] -l=<line>`

### Options

| Option              | Description                        |
|---------------------|------------------------------------|
| `-l, --line=<line>` | In-line statements to evaluate     |
| `-h, --help`        | Show this help message and exit    |
| `-V, --version`     | Print version information and exit |

### Example

```bash
ktest eval -l="key = aeskey(); info(\"Key: \", key); encrypted = aesenc(\"Clear Password\", key); info(\"Encrypted: \", encrypted)"
```

---

## scan - Scan Topics

Scan topic(s) to extract a sample test case.

**Usage:** `ktest scan [-hV] [-c=<config>] -e=<env> -i=<inputs> [-o=<output>]`

### Options

| Option                  | Description                                               | Default        |
|-------------------------|-----------------------------------------------------------|----------------|
| `-c, --config=<config>` | Path of the config file                                   | `ktconfig.yml` |
| `-e, --env=<env>`       | Name of the environment to use                            | *(required)*   |
| `-i, --inputs=<inputs>` | List of `'topic@broker,...'` (or `'@broker'` ref) to scan | *(required)*   |
| `-o, --output=<output>` | Path of output sample file                                | `ktsample.yml` |
| `-h, --help`            | Show this help message and exit                           |                |
| `-V, --version`         | Print version information and exit                        |                |

---

## mcp - MCP server mode

Starts in MCP stdio server mode.

**Usage:** `ktest mcp [-hV] [-c=<config>]`

### Options

| Option                  | Description                                               | Default        |
|-------------------------|-----------------------------------------------------------|----------------|
| `-c, --config=<config>` | Path of the config file                                   | `ktconfig.yml` |
| `-h, --help`            | Show this help message and exit                           |                |
| `-V, --version`         | Print version information and exit                        |                |

### Connect

Example of a definition to connect to the MCP server:
```json
"ktest-mcp": {
  "type": "stdio",
  "command": "/ktest",
  "args": [ "mcp", "-c", "/ktconfig.yml" ],
  "disabled": false,
  "autoApprove": [],
  "timeout": 60
}
```

---

## Related Documentation

- [Configuration File](03-config-file.md) - ktconfig.yml reference
- [Test Case File](04-test-case-file.md) - ktestcase.yml reference
- [Scripting Reference](05-scripting/00-overview.md) - All scripting functions