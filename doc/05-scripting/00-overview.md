# Scripting Reference

## Overview

This section contains the scripting language reference for KTest.

### Table of Contents

- [Operators, Conditions & Tokens](00-overview.md) - Basic syntax
- [Cryptographic Functions](01-crypt-funcs.md) - AES encryption
- [Faker Functions](02-faker-funcs.md) - UUID, regex generation
- [Hash Functions](03-hash-funcs.md) - CRC32, MD5, SHA
- [Hex Functions](04-hex-funcs.md) - Hexadecimal conversion
- [Log Functions](05-log-funcs.md) - Logging utilities
- [Math Functions](06-math-funcs.md) - Mathematical operations
- [Misc Functions](07-misc-funcs.md) - Environment, goto, jq
- [Text Functions](08-text-funcs.md) - String manipulation
- [Time Functions](09-time-funcs.md) - Date/time handling

---

## Operators

| Operator | Example   | Result | Description                                               |
|----------|-----------|--------|-----------------------------------------------------------|
| `-`      | `-3`      | `-3`   | Unary minus operator: negates the number value.           |
| `+`      | `4+3`     | `7`    | Addition operator: adds numbers.                          |
| `-`      | `9-5`     | `4`    | Subtraction operator: subtracts second number from first. |
| `*`      | `2*3`     | `6`    | Multiplication operator: multiplies two numbers.          |
| `/`      | `5/2`     | `2.5`  | Division operator: divides first number by second.        |
| `=`      | `A=3.14`  | `3.14` | Assignment operator.                                      |
| `(`      | `3*(1+2)` | `9`    | Left brace: start increased priority.                     |
| `)`      | `-(-4)`   | `4`    | Right brace: ends increased priority.                     |

## Conditions

| Operator | Example       | Result | Description                                                                               |
|----------|---------------|--------|-------------------------------------------------------------------------------------------|
| `?`      | `cnd?stm`     | -      | Execute a statement only if condition is true (=1).                                       |
| `?:`     | `c?true:else` | -      | Ternary if: execute 'true' statement if condition is true, else execute 'else' statement. |
| `==`     | `"A"=="A"`    | `1`    | Equal: true if arguments are equal.                                                       |
| `!=`     | `5!=5`        | `0`    | Not Equal: true if arguments are different.                                               |
| `<=`     | `2<=1+1`      | `1`    | Lesser or Equal: true if left argument is smaller or equal to right argument.             |
| `<`      | `2<2`         | `0`    | Lesser: true if left argument is strictly smaller than right argument.                    |
| `>=`     | `0>=1`        | `0`    | Greater or Equal: true if left argument is greater or equal to right argument.            |
| `>`      | `3>2`         | `1`    | Greater: true if left argument is strictly greater than right argument.                   |

## Specials Tokens

| Token | Description                                              |
|-------|----------------------------------------------------------|
| `;`   | Ends the current in-line statement and starts a new one. |
