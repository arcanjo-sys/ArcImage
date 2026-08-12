# ArcImage File Format

## 1. Overview

An ArcImage file is a binary file structured as:

```text
┌──────────────────────────────┐
│            HEADER            │
├──────────────────────────────┤
│           METADATA           │
├──────────────────────────────┤
│             DATA             │
│                              │
│       Codec Version          │
│       Compressed Size        │
│       Compressed Data        │
└──────────────────────────────┘
```

The current codec version uses:

```text
RGB
+
Adaptive Filtering
+
RLE
+
DEFLATE
```

---

# 2. Header

The header is 12 bytes.

| Offset |    Size | Field         |
| -----: | ------: | ------------- |
| `0x00` | 4 bytes | Magic         |
| `0x04` |  1 byte | Version Major |
| `0x05` |  1 byte | Version Minor |
| `0x06` | 2 bytes | Width         |
| `0x08` | 2 bytes | Height        |
| `0x03` |  1 byte | Color Format  |
| `0x00` |  1 byte | Padding       |

---

## 2.1 Magic

The first four bytes identify the format:

```text
ARCX
```

In hexadecimal:

```text
41 52 43 58
```

---

## 2.2 Version

The major and minor format versions are stored in the header.

The exact interpretation must follow the corresponding specification version.

---

## 2.3 Width

Image width.

Type:

```text
uint16
```

The current implementation supports:

```text
1 - 65535 pixels
```

---

## 2.4 Height

Image height.

Type:

```text
uint16
```

The current implementation supports:

```text
1 - 65535 pixels
```

---

## 2.5 Color Format

The current implementation works with:

```text
RGB
```

Each pixel has:

```text
R G B
```

with 8 bits per component.

Total:

```text
24 bits/pixel
3 bytes/pixel
```

---

## 2.6 Padding

Byte reserved for future extensions.

In the current version:

```text
0x00
```

---

# 3. Metadata

Metadata is stored using chunks.

Structure:

```text
┌────────────┬────────────┬──────────────┐
│   Marker   │   Length   │     Data     │
│  4 bytes   │  2 bytes   │   variable   │
└────────────┴────────────┴──────────────┘
```

`Length` uses two bytes.

The maximum size of an individual chunk is:

```text
65535 bytes
```

---

# 4. AUTH

Identifies the author associated with file generation.

```text
AUTH
Length
Data
```

The content is stored as UTF-8 text by the current implementation.

---

# 5. SOFT

Identifies the software responsible for generating the file.

Conceptual example:

```text
SOFT
ArcImage
```

---

# 6. TIMS

Stores the timestamp associated with file creation.

The current implementation stores the timestamp as a 64-bit value.

---

# 7. DATA

The marker:

```text
DATA
```

indicates the beginning of the encoded data.

The current structure is:

```text
DATA
Codec Version
Compressed Size
Compressed Data
```

---

## 7.1 Codec Version

One byte identifies the codec implementation used to encode the data.

Na implementação ARC 2.0:

```text
0x20
```

This field allows future versions to use different compression pipelines.

---

## 7.2 Compressed Size

The compressed data size is stored as:

```text
uint32
```

The value represents only the `Compressed Data` block.

---

## 7.3 Compressed Data

The data uses the following pipeline:

```text
RGB
 ↓
Adaptive Filter
 ↓
RLE
 ↓
DEFLATE
```

Therefore, the content stored in `Compressed Data` is a DEFLATE stream whose decompressed content corresponds to the RLE stream.

---

# 8. Filtered Image Data

Before RLE, the image is organized by rows.

Each row has:

```text
Filter Type
Filtered RGB Bytes
```

Para uma imagem com largura `W`:

```text
Row Size = 1 + (W × 3)
```

The first byte identifies the filter.

Valores:

| Value | Filter  |
| ----: | ------- |
|   `0` | None    |
|   `1` | Sub     |
|   `2` | Up      |
|   `3` | Average |
|   `4` | Paeth   |

---

# 9. Filter None

No prediction is applied.

```text
Filtered = Raw
```

---

# 10. Filter Sub

Each component is calculated relative to the equivalent component three bytes earlier in the same row.

```text
Filtered = Raw - Left
```

For the first pixel:

```text
Left = 0
```

---

# 11. Filter Up

Each byte is calculated relative to the equivalent byte in the previous row.

```text
Filtered = Raw - Up
```

On the first row:

```text
Up = 0
```

---

# 12. Filter Average

The predictor is:

```text
floor((Left + Up) / 2)
```

Then:

```text
Filtered = Raw - Average(Left, Up)
```

---

# 13. Filter Paeth

The Paeth predictor uses:

```text
Left
Up
UpperLeft
```

to select the value closest to the prediction:

```text
P = Left + Up - UpperLeft
```

The value among `Left`, `Up`, and `UpperLeft` with the smallest distance from `P` is used.

---

# 14. RLE

The filtered stream is encoded using Run-Length Encoding.

The control byte has:

```text
bit 7 = tipo
bits 6-0 = quantidade - 1
```

### Literal

```text
0xxxxxxx
```

The count is:

```text
(control & 0x7F) + 1
```

followed by the literal bytes.

### Run

```text
1xxxxxxx
```

The count is:

```text
(control & 0x7F) + 1
```

followed by a single byte that will be repeated.

---

# 15. DEFLATE

After RLE, the result is compressed using DEFLATE.

The reference implementation uses:

```java
java.util.zip.Deflater
```

for encoding and:

```java
java.util.zip.Inflater
```

for decoding.

DEFLATE provides:

* LZ77-based compression;
* Huffman coding.

---

# 16. Decoding

The decoder performs:

```text
Compressed Data
       ↓
    Inflate
       ↓
      RLE
       ↓
Filtered Rows
       ↓
Reverse Filter
       ↓
     RGB
```

---

# 17. RGB Pixel Representation

Each pixel has three components:

```text
R G B
```

Each component has 8 bits.

Therefore:

```text
Pixel = 24 bits
```

For an image:

```text
Width = W
Height = H
```

The uncompressed RGB representation has:

```text
W × H × 3 bytes
```

The value above represents the pixel size before filtering and compression.

---

# 18. Endianness

Multi-byte integer values used by the format are stored in **Big Endian (network byte order)**.

Example:

```text
0x12345678
```

is stored as:

```text
12 34 56 78
```

---

# 19. Compatibility

A decoder must validate the format and codec versions before interpreting the data.

Files generated by incompatible pipelines must not be interpreted as compatible files.

Incompatible changes must result in a new format or codec version.

---

# 20. Future Extensions

Possible extensions:

* RGBA;
* grayscale;
* alpha compression;
* channel transforms;
* block compression;
* checksums;
* color profiles;
* thumbnails;
* additional metadata;
* streaming data.

These extensions must be documented before being incorporated into the stable specification.
