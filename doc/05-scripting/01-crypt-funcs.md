# Cryptographic Functions

## aesdec

Decrypts a base64-AES256 encrypted value.

| Parameter   | Description                     |
|-------------|---------------------------------|
| `encrypted` | Base64-encoded encrypted string |
| `key`       | Base64-encoded AES256 key       |

**Example:** `aesdec("B64CryptedIn", "B64Key")` → `"ClearText"`

## aesenc

Encrypts a value with AES256 and returns base64-encoded result.

| Parameter   | Description               |
|-------------|---------------------------|
| `clearText` | Plain text to encrypt     |
| `key`       | Base64-encoded AES256 key |

**Example:** `aesenc("ClearText", "B64Key")` → `"B64CryptedOut"`

## aeskey

Generates a new random base64-encoded AES256 key.

**Example:** `aeskey()` → `"ygrS4...ijP8="`
