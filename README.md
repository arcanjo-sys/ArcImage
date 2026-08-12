# 🖼️ ArcImage

> An experimental, lossless image format built from scratch in Java.

**ArcImage** is an experimental project that implements its own image format, including its binary structure, metadata, compression, and pixel reconstruction.

The main goal of the project is to study and experiment with image storage and compression techniques by creating its own implementation instead of relying exclusively on traditional formats.

> ⚠️ **Status:** experimental. The specification may still change during development.

---

## ✨ Highlights

ArcImage currently uses a hybrid compression pipeline:

```text
┌──────────────────────┐
│      Imagem RGB      │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│ Adaptive Filter      │
│ None / Sub / Up      │
│ Average / Paeth      │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│         RLE          │
│ Run-Length Encoding  │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│       DEFLATE        │
│    LZ77 + Huffman    │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│       File ARC       │
└──────────────────────┘
```

The reverse process restores the data exactly:

```text
ARC
 ↓
DEFLATE
 ↓
RLE
 ↓
Inverse filter
 ↓
RGB original
```

The format is designed for **lossless compression**. The goal is for the reconstructed image to have exactly the same pixel values as the original image.

---

# 🔐 Lossless Compression

ArcImage does not use quantization or deliberate quality reduction in the current version.

The codec's goal is:

```text
Image original
      ↓
    ARC
      ↓
Reconstructed image
```

com:

```text
pixel_original == pixel_reconstructed
```

for every pixel.

Therefore, size reduction does not depend on removing visual details from the image.

---

# 🧬 Format Architecture

An ARC file has a structure similar to:

```text
┌──────────────────────────────┐
│            HEADER            │
├──────────────────────────────┤
│           METADATA           │
│                              │
│ AUTH                         │
│ SOFT                         │
│ TIMS                         │
├──────────────────────────────┤
│             DATA             │
├──────────────────────────────┤
│        CODEC VERSION         │
├──────────────────────────────┤
│     COMPRESSED DATA SIZE     │
├──────────────────────────────┤
│       COMPRESSED DATA        │
└──────────────────────────────┘
```

---

# 📦 Header

The header is currently **12 bytes**.

| Offset | Size | Field         |
| -----: | ------: | ------------- |
| `0x00` | 4 bytes | Signature     |
| `0x04` |  1 byte | Version Major |
| `0x05` |  1 byte | Version Minor |
| `0x06` | 2 bytes | Width         |
| `0x08` | 2 bytes | Height        |
| `0x0A` |  1 byte | Color format  |
| `0x0B` |  1 byte | Padding       |

### Signature

The signature used by the format is:

```text
ARCX
```

---

# 📐 Dimensões

Width and height are stored as **UInt16 Big Endian**.

Example:

```text
Width:
[HIGH BYTE][LOW BYTE]

Height:
[HIGH BYTE][LOW BYTE]
```

The current implementation supports dimensions up to:

```text
65535 × 65535
```

limited by the UInt16 representation used by the format.

---

# 🎨 Cor

The current implementation works with:

```text
RGB
```

using:

```text
8 bits Red
8 bits Green
8 bits Blue
```

Total:

```text
24 bits por pixel
```

Alpha/transparency is not yet part of the current codec's main pipeline.

---

# 🏷️ Metadados

The format has a metadata structure based on:

```text
TAG + SIZE + DATA
```

Where:

```text
TAG  = 4 bytes
SIZE = UInt16
DATA = content
```

Fields currently used include:

### `AUTH`

Author responsible for creating the file.

### `SOFT`

Software used to generate the file.

### `TIMS`

File creation timestamp.

### `DATA`

Marks the beginning of the compressed image data.

---

# 🗜️ Compression Pipeline

## 1. Adaptive Filter

Each image row is analyzed using five filters:

```text
0 = None
1 = Sub
2 = Up
3 = Average
4 = Paeth
```

The encoder calculates all five possibilities and selects the one that produces the lowest cost for the row.

This transforms spatial patterns into smaller and more repetitive residual values.

For example:

```text
100 101 102 103 104 105
```

pode se transformar em algo próximo de:

```text
100 1 1 1 1 1
```

Essa representação é muito mais favorável para os estágios seguintes.

---

## 2. RLE

After filtering, **Run-Length Encoding** is applied.

Repeated sequences can be represented by:

```text
value + amount
```

For example:

```text
00 00 00 00 00
```

It can be conceptually represented as:

```text
RUN(5, 00)
```

The current RLE operates on the bytes produced by the filtering stage.

---

## 3. DEFLATE

After RLE, the data is passed through **DEFLATE**.

DEFLATE combines:

* LZ77
* Huffman coding

The current implementation uses Java's standard-library `Deflater`.

Therefore, the final pipeline is:

```text
Filter
   ↓
RLE
   ↓
DEFLATE
   ├── LZ77
   └── Huffman
```

---

# 🔄 Decoding

The decoder performs the reverse process:

```text
┌──────────────────────┐
│      File ARC        │
└──────────┬───────────┘
           ↓
       read Header
           ↓
       read Metadata
           ↓
        DEFLATE
           ↓
          RLE
           ↓
    Restore filters
           ↓
      Restore RGB
           ↓
     BufferedImage
```

The reconstructed image can then be used normally through the Java image API.

---

# 💻 Technologies

The project currently uses:

* Java
* `BufferedImage`
* `ImageIO`
* `Deflater`
* `Inflater`
* `Picocli`
* Maven
* Git
* GitHub
* IntelliJ IDEA

No external libraries are required for the current codec core.

---

# 📂 Project Structure

The main structure is organized approximately as follows:

```text
ArcImage/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── arcanjo/
│                   └── archimage/
│                       ├── codec/
│                       │   ├── ArcDecode.java
│                       │   └── ArcEncode.java
│                       │
│                       ├── cli/
│                       │   ├── ArcCommand.java
│                       │   ├── DecodeCommand.java
│                       │   ├── EncodeCommand.java
│                       │   └── ViewCommand.java
│                       │
│                       ├── format/
│                       │   └── Format.java
│                       │
│                       └── viewer/
│                           └── ImageView.java
│
├── Main.java
├── pom.xml
├── README.md
├── CONTRIBUTING.md
└── LICENSE
```

---

# 🛠️ Current Status

| Recurso                       | Status                |
| ----------------------------- | --------------------- |
| Basic format structure     | 🟢 Implemented       |
| Header                        | 🟢 Implemented       |
| RGB                           | 🟢 Implemented       |
| Encoder                       | 🟢 Implemented       |
| Decoder                       | 🟢 Implemented       |
| Metadata                     | 🟢 Implemented       |
| Adaptive filters           | 🟢 Implemented       |
| RLE                           | 🟢 Implemented       |
| DEFLATE                       | 🟢 Implemented       |
| Compression lossless           | 🟢 Implemented       |
| Automated Benchmark          | 🟡 In development |
| Alpha / RGBA                  | 🔴 Planned          |
| Codec independente de Java    | 🔴 Future             |
| CLI tools               | 🔴 Planned          |
| Own viewer          | 🔴 Future             |
| Stable Binary Specification | 🟡 In development |

---

# 🗺️ Roadmap

## ARC 2.x

* [x] Basic format structure
* [x] Header
* [x] RGB
* [x] Encoder
* [x] Decoder
* [x] Metadata
* [x] Adaptive filters
* [x] RLE
* [x] DEFLATE
* [x] Compression lossless
* [ ] Automated Benchmark
* [ ] Pixel-by-pixel integrity testing
* [ ] RLE Optimization
* [ ] Adaptive choice of strategies
* [ ] Supports RGBA images

## ARC 3.x

Potential experiments:

* Additional predictors
* Channel transformation
* Pixel-oriented RLE
* Adaptive block compression
* Better filter selection
* New compression modes
* Speed ​​optimization
* Parallelization
* Stable Binary Specification

---

# 🧪 Benchmark

ArcImage benchmarks should consider at least:

```text
Original size
PNG size
ARC size
Compression ratio
Compression time
Decompression time
Pixel integrity
```

Integrity should be validated by comparing the original image with the reconstructed image pixel by pixel.

Conceptual example:

```java
original.getRGB(x, y)
    ==
decoded.getRGB(x, y)
```

for all valid positions in the image.

---

# ⚠️ Experimental Project

ArcImage is still under development.

The binary specification, compression algorithms, and internal file structure may change without a guarantee of compatibility between versions.

Files generated by experimental versions may not be compatible with future codec versions.

****Do not use the current format for critical data storage without keeping a copy of the original image.****

---

# 🤝 Contributions

Contributions, ideas, and experiments are welcome.

Some particularly interesting areas include:

* new image filters;
* prediction algorithms;
* adaptive RLE;
* LZ77;
* Huffman;
* channel transforms;
* memory optimization;
* speed optimization;
* comparative tests against PNG, WebP, AVIF, and JPEG XL;
* ARC format analysis tools.

---

# 📜 License

This project is released under the **MIT License**.

See the `LICENSE` file for the complete terms.

---

# 👨‍💻 Project

**ArcImage**

An experimental image format built from scratch to study:

```text
binary formats
        +
image processing
        +
lossless compression
        +
codec development
```

> Building an image format from scratch, one step at a time.
