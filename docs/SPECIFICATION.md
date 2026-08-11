# ArcImage File Format Specification

## 1. Introdução

## 2. Estrutura do arquivo

### 2.1 Header

### 2.2 Metadata

### 2.3 Pixel Data

### 2.4 Optional Data

## 3. Header

| Campo       | Tamanho | Tipo   | Descrição |
|-------------|--------:|--------|---|
| Magic       | 4 bytes | ASCII  | Identificador ArcImage |
| Version     | 2 bytes | uint16 | Versão do formato |
| Width       | 2 bytes | uint16 | Largura |
| Height      | 2 bytes | uint16 | Altura |
| Color Depth |  1 byte | uint8  | Profundidade de cor |
| Padding     | 1 byte  | uint8  | Reservado |

## 4. Pixel Format

## 5. Endianness

## 6. Metadata

| Campo       |   Tamanho | Tipo   | Descrição           |
|-------------|----------:|--------|---------------------|
| AUTH        |   4 bytes | ASCII  | Marcador da Chunck  |
| Length      |   2 bytes | uint16 | Tamanho do Texto    |
| Text        | undetermined | ASCII  | Texto               |

| Campo  | Tamanho | Tipo   | Descrição           |
|--------|--------:|--------|---------------------|
| TIMS   | 4 bytes | ASCII  | Marcador da Chunck  |
| Length | 2 bytes | uint16 | Tamanho do Texto    |
| Text   | 4 bytes | ASCII  | Texto               |

| Campo  |   Tamanho | Tipo   | Descrição                               |
|--------|----------:|--------|-----------------------------------------|
| SOFT   |   4 bytes | ASCII  | Marcador do Software que gerou a imagem |
| Length |   2 bytes | uint16 | Tamanho do Texto                        |
| Text   | undetermined | ASCII  | Texto                                   |

| Campo  | Tamanho | Tipo   | Descrição             |
|--------|--------:|--------|-----------------------|
| COPY   | 4 bytes | ASCII  | Marcador de Copyright |
| Length | 2 bytes | uint16 | Tamanho do Texto      |
| Text   | undetermined | ASCII  | Texto                 |

## 7. Compression

## 8. Compatibility

## 9. Versioning