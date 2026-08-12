# ArcImage Roadmap

## Vision

The goal of ArcImage is to evolve from an experimental codec into a well-defined, documented, testable image format that can be implemented independently of the Java reference implementation.

The roadmap represents the project's current direction and may change based on experiment results.

---

# Phase 1 — Core Format

* [x] Magic
* [x] Version
* [x] Width
* [x] Height
* [x] Color format
* [x] Padding
* [x] RGB representation
* [x] Encoder
* [x] Decoder
* [x] Viewer

---

# Phase 2 — Metadata

* [x] AUTH
* [x] SOFT
* [x] TIMS
* [ ] COPY
* [ ] Formal metadata ordering
* [ ] Unknown chunk handling
* [ ] Metadata limits
* [ ] Formal encoding definition

---

# Phase 3 — Lossless Compression

* [x] Adaptive filtering
* [x] None filter
* [x] Sub filter
* [x] Up filter
* [x] Average filter
* [x] Paeth filter
* [x] Per-row filter selection
* [x] RLE
* [x] DEFLATE
* [x] LZ77 through DEFLATE
* [x] Huffman coding through DEFLATE
* [x] Decoder pipeline
* [ ] Adaptive RLE strategy
* [ ] Compression benchmarks
* [ ] Compression speed benchmarks
* [ ] Memory benchmarks

---

# Phase 4 — Validation

* [ ] Pixel-by-pixel round-trip tests
* [ ] Random image tests
* [ ] Solid-color tests
* [ ] Gradient tests
* [ ] High-detail photographic tests
* [ ] Noise tests
* [ ] Large image tests
* [ ] Corrupted file tests
* [ ] Truncated file tests
* [ ] Invalid codec tests
* [ ] Invalid RLE tests
* [ ] Invalid filter tests

---

# Phase 5 — Benchmarking

A standardized benchmark suite will compare ArcImage against other lossless image representations.

The benchmark should control:

* image dimensions;
* color depth;
* RGB/RGBA representation;
* source pixels;
* compression level;
* output size;
* encoding time;
* decoding time;
* memory usage.

Initial comparison target:

```text
PNG RGB  ↔  ARC RGB
```

Future comparison:

```text
PNG RGBA  ↔  ARC RGBA
```

Benchmark results should not be generalized from a small number of images.

---

# Phase 6 — RGBA

* [ ] RGBA pixel format
* [ ] Alpha channel encoding
* [ ] Alpha channel filtering
* [ ] Alpha channel compression
* [ ] RGBA decoding
* [ ] RGBA integrity tests
* [ ] RGB vs RGBA benchmark parity

---

# Phase 7 — Compression Improvements

Potential experiments:

* [ ] Pixel-oriented RLE
* [ ] Channel-oriented RLE
* [ ] Separate RGB channels
* [ ] Channel transforms
* [ ] Block-based compression
* [ ] Better filter cost functions
* [ ] Adaptive choice between compression strategies
* [ ] Compression strategy selection based on image statistics
* [ ] Additional entropy coding experiments

The encoder should prefer the smallest valid representation rather than assuming that a single compression strategy is optimal for every image.

---

# Phase 8 — Integrity

* [ ] Checksum evaluation
* [ ] CRC evaluation
* [ ] Hash evaluation
* [ ] Corruption detection
* [ ] Integrity metadata
* [ ] Formal integrity specification

---

# Phase 9 — Tooling

* [x] CLI
* [x] PNG → ARC
* [x] JPEG → ARC
* [x] ARC → image
* [x] ARC viewer
* [ ] ARC file inspector
* [ ] Compression statistics command
* [ ] Benchmark command
* [ ] Hex dump / format inspection
* [ ] Test file generator

---

# Phase 10 — Specification

* [x] Initial file format documentation
* [x] Initial architecture documentation
* [x] Compression documentation
* [ ] Finalize endianness rules
* [ ] Finalize pixel formats
* [ ] Finalize metadata rules
* [ ] Finalize codec versioning
* [ ] Define compatibility guarantees
* [ ] Define reference files
* [ ] Define binary examples
* [ ] Publish stable specification

---

# Phase 11 — Future

Possible long-term goals:

* [ ] Streaming encoder
* [ ] Streaming decoder
* [ ] Parallel encoding
* [ ] Parallel decoding
* [ ] Native implementation
* [ ] Implementations in other languages
* [ ] Library API
* [ ] Browser/tooling support
* [ ] Image editor integration

---

# Current Status

## ARC 2.0

The current experimental codec provides:

```text
RGB
 ↓
Adaptive Filtering
 ↓
RLE
 ↓
DEFLATE
 ↓
ARC
```

The corresponding decoder performs the inverse pipeline.

The implementation is currently considered **experimental but functional**.

The next major technical milestones are:

1. pixel-perfect automated tests;
2. controlled RGB benchmark against PNG;
3. RGBA support;
4. adaptive compression strategies;
5. stable binary specification.
