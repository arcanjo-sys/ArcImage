# ArcImage Development Guide

## 1. About This Document

> This document describes how to set up the ArcImage development environment and how to work on the project.

The goal is to facilitate contributions and maintain a consistent development process.

---

# 2. Requirements

Before starting, install:

* Java;
* Maven;
* Git.

The minimum Java version should be checked in the project's `pom.xml`.

---

# 3. Getting the Code

Clone the repository:

```bash
git clone https://github.com/arcanjo-sys/ArcImage.git
```

Enter the directory:

```bash
cd ArcImage
```

---

# 4. Build

To build the project:

```bash
mvn clean package
```

To compile only:

```bash
mvn compile
```

---

# 5. Testing

Run the tests with:

```bash
mvn test
```

Tests should primarily verify:

* file creation;
* file reading;
* Header values;
* dimensions;
* color depth;
* pixel reading and writing;
* chunks;
* invalid files.

---

# 6. Project Structure

The structure may be organized as follows:

```text
ArcImage/
│
├── docs/
│   ├── SPECIFICATION.md
│   ├── FILE_FORMAT.md
│   ├── ARCHITECTURE.md
│   ├── DEVELOPMENT.md
│   └── ROADMAP.md
│
├── src/
│   ├── main/
│   │   └── java/
│   │
│   └── test/
│       └── java/
│
├── README.md
├── CONTRIBUTING.md
├── LICENSE
├── pom.xml
└── .gitignore
```

The actual Java package structure should be kept as the primary reference for this section.

---

# 7. Format Development

Changes to the binary structure should be handled carefully.

Antes de modificar o formato:

1. Update the specification.
2. Define the impact of the change.
3. Update the Encoder.
4. Update the Decoder.
5. Update the Viewer
6. Add or update tests.
7. Check compatibility.
8. Update the documentation.

---

# 8. Adding a New Chunk

A new chunk must have:

```text
┌──────────────┬──────────────┬─────────────────┐
│    Marker    │    Length    │      Data       │
│   4 bytes    │   2 bytes    │    variable     │
└──────────────┴──────────────┴─────────────────┘
```

The Marker must be exactly 4 bytes.

Example:

```text
EXIF
```

Before adding a new chunk, document:

* name;
* purpose;
* size;
* type;
* encoding;
* validation rules;
* compatibility.

---

# 9. Compatibility

Changes incompatible with previous versions must result in a new format version.

Example:

```text
Version 1
    │
    ├── compatible changes
    │
    ▼
Version 1

    │
    └── incompatible changes
            │
            ▼
        Version 2
```

---

# 10. Pull Requests

Pull Requests should explain:

* what was changed;
* por que foi alterado;
* which files were modified;
* which tests were run;
* whether the binary format changed.

Format changes must include an update to the corresponding documentation.

---

# 11. Commits

Prefer small, focused commits.

Examples:

```text
feat: add AUTH metadata chunk
fix: validate image dimensions
docs: update file format specification
test: add decoder tests
refactor: simplify pixel reader
```

---

# 12. Debugging

During binary-format development, it is useful to inspect files directly.

Example:

```bash
xxd image.arc
```

ou:

```bash
hexdump -C image.arc
```

This makes it possible to compare the bytes produced by the Encoder with the specification.

---

# 13. Compatibility Tests

Test files should be maintained for relevant format versions.

Example:

```text
tests/
├── valid/
│   ├── minimal.arc
│   ├── metadata.arc
│   └── rgb.arc
│
└── invalid/
    ├── invalid-magic.arc
    ├── truncated-header.arc
    └── invalid-version.arc
```

These files help prevent implementation regressions.

---

# 14. Checklist

Before submitting a change:

* [ ] The project builds.
* [ ] The tests pass.
* [ ] New behaviors have tests.
* [ ] The specification has been updated.
* [ ] The documentation has been updated.
* [ ] Compatibility has been checked.
* [ ] The code does not depend on undocumented format details.
