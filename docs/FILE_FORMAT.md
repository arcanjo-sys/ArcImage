# ArcImage File Format

## 1. Overview

An **ArcImage** file is a binary file structured into different sections.

The basic structure consists of:

```text
┌──────────────────────────────┐
│            HEADER            │
├──────────────────────────────┤
│           METADATA           │
├──────────────────────────────┤
│          Pixel Data          │
├──────────────────────────────┤
│         OPTIONAL DATA        │
└──────────────────────────────┘
```

The order and presence of optional sections depend on the format version.

---

## 2. Header

The Header contains the information required for a decoder to identify and interpret the file.

### Estrutura

| Field       | Size | Type   | Description                 |
| ----------- | ------: | ------ | ------------------------- |
| Magic       | 4 bytes | ASCII  | Format identifier  |
| Version     | 2 bytes | uint16 | Format version         |
| Width       | 2 bytes | uint16 | Image width         |
| Height      | 2 bytes | uint16 | Image height          |
| Color Depth |  1 byte | uint8  | Color depth       |
| Padding     |  1 byte | uint8  | Reserved for future use |

Total:

```text
12 bytes
```

### Magic

The first 4 bytes of the file identify the format as ArcImage.

The value used is:

```text
ARCX
```


### Version

Represents the specification version used to create the file.

The field is 2 bytes:

```text
uint16
```

Example:

```text
0x0001
```

represents version `1`.

### Width

Image width in pixels.

Type:

```text
uint16
```

The maximum representable value is:

```text
65535 pixels
```

### Height

Image height in pixels.

Type:

```text
uint16
```

The maximum representable value is:

```text
65535 pixels
```

### Color Depth

Indicates the number of bits used to represent the color information of each pixel.

Possible examples:

```text
03
```

Valid values must be defined by the corresponding specification version.

### Padding

Byte reserved for future format extensions.

In current versions, this field must be written as:

```text
0x00
```

A decoder should ignore this field's value if the version specification does not define another meaning.

---

# 3. Metadata

Metadata is stored using structures called **chunks**.

Each chunk has a general structure:

```text
┌──────────────┬──────────────┬─────────────────────┐
│    Marker    │    Length    │        Data         │
│   4 bytes    │   2 bytes    │     variable        │
└──────────────┴──────────────┴─────────────────────┘
```

Where:

* `Marker` identifies the chunk type;
* `Length` specifies the content size;
* `Data` contains the stored information.

---

## 3.1 AUTH

Stores the image author.

```text
┌──────────┬──────────┬────────────────┐
│   AUTH   │  Length  │      Text      │
│ 4 bytes  │ 2 bytes  │    variable    │
└──────────┴──────────┴────────────────┘
```

| Field  |  Size | Type   |
| ------ | -------: | ------ |
| AUTH   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variable | ASCII  |

Conceptual example:

```text
AUTH
0005
Alice
```

---

## 3.2 TIMS

Stores the time-related information associated with the image.

```text
┌──────────┬──────────┬──────────────┐
│   TIMS   │  Length  │     Text     │
│ 4 bytes  │ 2 bytes  │   4 bytes    │
└──────────┴──────────┴──────────────┘
```

| Field  |  Size | Type   |
| ------ |---------:| ------ |
| TIMS   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variable | ASCII  |

The exact meaning of `TIMS` content must be defined by the version specification.

---

## 3.3 SOFT

Identifies the software responsible for generating the image.

```text
┌──────────┬──────────┬────────────────┐
│   SOFT   │  Length  │      Text      │
│ 4 bytes  │ 2 bytes  │    variable    │
└──────────┴──────────┴────────────────┘
```

| Field  |  Size | Type   |
| ------ | -------: | ------ |
| SOFT   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variable | ASCII  |

Example:

```text
SOFT
0007
ArcImage
```

---

## 3.4 COPY

Stores copyright information.

```text
┌──────────┬──────────┬────────────────┐
│   COPY   │  Length  │      Text      │
│ 4 bytes  │ 2 bytes  │    variable    │
└──────────┴──────────┴────────────────┘
```

| Field  |  Size | Type   |
| ------ | -------: | ------ |
| COPY   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variable | ASCII  |

Example:

```text
COPY
0011
Copyright 2026
```

---

# 4. Pixel Data

After Metadata, the file may contain pixel data.

Pixel representation depends on the `Color Depth` field.

For example, for a 24-bit RGB image:

```text
Pixel 0:
R G B

Pixel 1:
R G B

Pixel 2:
R G B
```

An image with width `W` and height `H` has:

```text
W × H
```

pixels.

For a 24-bit RGB representation:

```text
Pixel Data Size = Width × Height × 3
```

---

# 5. Optional Data

Optional data consists of information that is not required to represent the image pixels.

Future examples:

* thumbnails;
* camera information;
* color profiles;
* editing information;
* application data;
* application-specific extensions.

This data should use identifiable structures and must not prevent a basic decoder from reading the essential image data.

---

# 6. Data Order

An implementation may organize the file as follows:

```text
HEADER
METADATA
PIXEL DATA
OPTIONAL DATA
```

Example:

```text
┌────────────────────┐
│ Header             │
├────────────────────┤
│ AUTH chunk         │
├────────────────────┤
│ TIMS chunk         │
├────────────────────┤
│ SOFT chunk         │
├────────────────────┤
│ COPY chunk         │
├────────────────────┤
│ Pixel Data         │
├────────────────────┤
│ Optional chunks    │
└────────────────────┘
```

The definitive order should be considered part of the specification once the format reaches a stable version.

---

# 7. Integrity

A future version of ArcImage may add integrity mechanisms such as:

* checksum;
* CRC;
* hash;
* detection of incomplete or corrupted files.

These mechanisms are not yet part of the currently defined basic structure.

---

# 8. Extensibility

New chunks may be added without necessarily changing the Header.

When possible, a reader that encounters an unknown chunk should skip it using the `Length` field to determine where the next chunk begins.

This allows future versions to add functionality without completely breaking older implementations.
