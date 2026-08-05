# Hash Functions

## crc32

Returns the CRC-32 hash of the string parameter.

| Parameter | Description    |
|-----------|----------------|
| `input`   | String to hash |

**Example:** `crc32("SampleString")` → `"3ca8bf4"`

## decode64

Returns the decoded text of a base64-encoded string.

| Parameter | Description           |
|-----------|-----------------------|
| `input`   | Base64-encoded string |

**Example:** `decode64("VGV4dA==")` → `"Text"`

## encode64

Returns the base64 encoding of a string.

| Parameter | Description      |
|-----------|------------------|
| `input`   | String to encode |

**Example:** `encode64("SampleString")` → `"U2FtcGxlU3RyaW5n"`

## md5

Returns the MD5 hash of the string parameter.

| Parameter | Description    |
|-----------|----------------|
| `input`   | String to hash |

**Example:** `md5("SampleString")` → `"ec1dd92925cb06934c047fb3f5380cba"`

## sha1

Returns the SHA-1 hash of the string parameter.

| Parameter | Description    |
|-----------|----------------|
| `input`   | String to hash |

**Example:** `sha1("SampleString")` → `"ac7fc7261c573830...f20bf0d74d1443cd"`

## sha256

Returns the SHA-256 hash of the string parameter.

| Parameter | Description    |
|-----------|----------------|
| `input`   | String to hash |

**Example:** `sha256("SampleString")` → `"77b12c9c6213a05f...8b2c34769ec4fc20"`

## sha512

Returns the SHA-512 hash of the string parameter.

| Parameter | Description    |
|-----------|----------------|
| `input`   | String to hash |

**Example:** `sha512("SampleString")` → `"aee8e20df4b3ce73...e7e03c6fcda75961"`
