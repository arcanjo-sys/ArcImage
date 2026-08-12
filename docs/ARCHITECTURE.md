# ArcImage Architecture

## 1. Overview

ArcImage is an experimental image format focused on **lossless compression**, its own binary representation, and an implementation independent of Java's internal image structures.

The current architecture consists of:

* image representation;
* predictive filters;
* RLE;
* DEFLATE;
* encoder;
* decoder;
* viewer;
* metadata;
* binary file structure.

The current implementation works with **24-bit-per-pixel RGB** images.

---

## 2. Encoding Pipeline

The current version uses the following pipeline:

```text
                 RGB Image
                    │
                    ▼
          ┌──────────────────┐
          │ Adaptive Filters │
          │                  │
          │ None             │
          │ Sub              │
          │ Up               │
          │ Average          │
          │ Paeth            │
          └────────┬─────────┘
                   │
                   ▼
          ┌──────────────────┐
          │       RLE        │
          └────────┬─────────┘
                   │
                   ▼
          ┌──────────────────┐
          │     DEFLATE      │
          │                  │
          │ LZ77 + Huffman   │
          └────────┬─────────┘
                   │
                   ▼
               ARC File
```

The process is lossless.

No pixel information is discarded during compression.

---

## 3. Decoding Pipeline

The decoder performs the reverse process:

```text
               ARC File
                  │
                  ▼
             Read Header
                  │
                  ▼
             Read Metadata
                  │
                  ▼
             Read DATA
                  │
                  ▼
              DEFLATE
                  │
                  ▼
                 RLE
                  │
                  ▼
          Reverse Filtering
                  │
                  ▼
                RGB
                  │
                  ▼
            BufferedImage
```

---

## 4. Adaptive Filtering

Before compression, each image row is analyzed using five filters.

```text
0 = None
1 = Sub
2 = Up
3 = Average
4 = Paeth
```

For each row, the encoder generates all five representations and calculates a cost for each one.

The representation with the lowest cost is selected.

### None

No prediction applies.

```text
F(x) = Raw(x)
```

### Sub

Predicts the pixel using the value on the left.

```text
F(x) = Raw(x) - Left(x)
```

### Up

Predicts the pixel using the value from the previous row.

```text
F(x) = Raw(x) - Up(x)
```

### Average

Uses the average of the values on the left and above.

```text
F(x) = Raw(x) - floor((Left(x) + Up(x)) / 2)
```

### Paeth

Use the Paeth predictor based on:

```text
Left
Up
UpperLeft
```

These filters transform image data into more predictable residuals.

---

## 5. RLE

After filtering, the result passes through Run-Length Encoding.

RLE represents repeated byte sequences compactly.

The encoder avoids using RLE when a repetition provides no benefit.

Structurally:

```text
Control Byte
     │
     ├── Literal
     │
     └── Run
```

The decoder restores the original bytes before the reverse-filtering stage.

---

## 6. DEFLATE

After RLE, the data is processed by the DEFLATE algorithm.

The current implementation uses:

```java
java.util.zip.Deflater
```

and the decoder uses:

```java
java.util.zip.Inflater
```

DEFLATE combines:

* LZ77;
* Huffman coding.

Thus, the complete architecture combines three levels of compression:

```text
Spatial Prediction
        +
Run-Length Encoding
        +
LZ77/Huffman
```

---

## 7. Encoder

The encoder is responsible for:

1. load the image;
2. validate dimensions;
3. convert pixels to RGB;
4. generate adaptive filters;
5. apply RLE;
6. apply DEFLATE;
7. build the header;
8. write metadata;
9. write the `DATA` block;
10. generate the `.arc` file.

---

## 8. Decoder

The decoder is responsible for:

1. validate the signature;
2. read dimensions;
3. identify the codec;
4. read metadata;
5. read the compressed block;
6. run DEFLATE;
7. run reverse RLE;
8. restore filters;
9. reconstruct RGB pixels;
10. produce the final image.

---

## 9. Viewer

The Viewer uses the decoder to reconstruct the image and present it to the user.

The Viewer should not implement a second decoding logic.

The recommended architecture is:

```text
ARC File
   │
   ▼
Decoder
   │
   ▼
BufferedImage
   │
   ▼
Viewer
```

This prevents discrepancies between decoder and viewer behavior.

---

## 10. File Structure

The current logical structure is:

```text
HEADER
METADATA
DATA
    CODEC VERSION
    COMPRESSED SIZE
    COMPRESSED DATA
```

`DATA` marks the beginning of the compressed representation.

---

## 11. Metadata

Metadata uses chunks:

```text
┌────────────┬────────────┬──────────────┐
│   Marker   │   Length   │     Data     │
│  4 bytes   │  2 bytes   │   variable   │
└────────────┴────────────┴──────────────┘
```

Chunks currently used by the implementation include:

```text
AUTH
SOFT
TIMS
```

New chunks may be added in the future.

---

## 12. Error Handling

The decoder must reject files with:

* invalid signature;
* invalid dimensions;
* unknown codec;
* truncated compressed data;
* invalid RLE;
* invalid filters;
* inconsistent size;
* incomplete image data.

Format errors should be handled separately from I/O errors whenever possible.

---

## 13. Lossless Guarantee

The fundamental property of the codec is:

```text
Original RGB
     ==
Decoded RGB
```

for every pixel.

The recommended validation is a pixel-by-pixel comparison:

```java
original.getRGB(x, y) == decoded.getRGB(x, y)
```

across the entire image.

---

## 14. Extensibility

The architecture was designed to allow future extensions:

* RGBA;
* New filters;
* New compression algorithms;
* Channel transformation;
* Checksums;
* Color profiles;
* Thumbnails;
* Streaming;
* Block compression.

Incompatible extensions must be identified through format or codec versioning.

---

## 15. Principles

### Lossless

No pixel should be lost during compression.

### Simplicidade

The structure should remain understandable and inspectable.

### Extensibility

The format should allow new functionality without compromising its basic structure.

### Portabilidade

The specification should be independent of the Java implementation.

### Determinismo

The encoding process should behave predictably for a given input and configuration.

### Documentation

The specification should be sufficient to enable independent codec implementations.
