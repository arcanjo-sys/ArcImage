# ArcImage File Format

## 1. Visão geral

Um arquivo ArcImage é um arquivo binário estruturado em:

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

A versão atual do codec utiliza:

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

O header possui 12 bytes.

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

Os primeiros quatro bytes identificam o formato:

```text
ARCX
```

Em hexadecimal:

```text
41 52 43 58
```

---

## 2.2 Version

A versão principal e secundária do formato são armazenadas no header.

A interpretação exata deve seguir a versão correspondente da especificação.

---

## 2.3 Width

Largura da imagem.

Tipo:

```text
uint16
```

A implementação atual suporta:

```text
1 - 65535 pixels
```

---

## 2.4 Height

Altura da imagem.

Tipo:

```text
uint16
```

A implementação atual suporta:

```text
1 - 65535 pixels
```

---

## 2.5 Color Format

A implementação atual trabalha com:

```text
RGB
```

Cada pixel possui:

```text
R G B
```

com 8 bits por componente.

Total:

```text
24 bits/pixel
3 bytes/pixel
```

---

## 2.6 Padding

Byte reservado para futuras extensões.

Na versão atual:

```text
0x00
```

---

# 3. Metadata

Os metadados são armazenados utilizando chunks.

Estrutura:

```text
┌────────────┬────────────┬──────────────┐
│   Marker   │   Length   │     Data     │
│  4 bytes   │  2 bytes   │   variável   │
└────────────┴────────────┴──────────────┘
```

`Length` utiliza dois bytes.

O tamanho máximo de um chunk individual é:

```text
65535 bytes
```

---

# 4. AUTH

Identifica o autor associado à geração do arquivo.

```text
AUTH
Length
Data
```

O conteúdo é armazenado como texto UTF-8 pela implementação atual.

---

# 5. SOFT

Identifica o software responsável pela geração do arquivo.

Exemplo conceitual:

```text
SOFT
ArcImage
```

---

# 6. TIMS

Armazena o timestamp associado à criação do arquivo.

A implementação atual armazena o timestamp como um valor de 64 bits.

---

# 7. DATA

O marker:

```text
DATA
```

indica o início dos dados codificados.

A estrutura atual é:

```text
DATA
Codec Version
Compressed Size
Compressed Data
```

---

## 7.1 Codec Version

Um byte identifica a implementação do codec utilizada para codificar os dados.

Na implementação ARC 2.0:

```text
0x20
```

Esse campo permite que futuras versões utilizem pipelines de compressão diferentes.

---

## 7.2 Compressed Size

O tamanho dos dados comprimidos é armazenado como:

```text
uint32
```

O valor representa somente o bloco `Compressed Data`.

---

## 7.3 Compressed Data

Os dados possuem o seguinte pipeline:

```text
RGB
 ↓
Adaptive Filter
 ↓
RLE
 ↓
DEFLATE
```

Portanto, o conteúdo armazenado em `Compressed Data` é um stream DEFLATE cujo conteúdo descompactado corresponde ao stream RLE.

---

# 8. Filtered Image Data

Antes do RLE, a imagem é organizada por linhas.

Cada linha possui:

```text
Filter Type
Filtered RGB Bytes
```

Para uma imagem com largura `W`:

```text
Row Size = 1 + (W × 3)
```

O primeiro byte identifica o filtro.

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

Nenhuma predição é aplicada.

```text
Filtered = Raw
```

---

# 10. Filter Sub

Cada componente é calculado em relação ao componente equivalente três bytes anteriormente na mesma linha.

```text
Filtered = Raw - Left
```

Para o primeiro pixel:

```text
Left = 0
```

---

# 11. Filter Up

Cada byte é calculado em relação ao byte equivalente da linha anterior.

```text
Filtered = Raw - Up
```

Na primeira linha:

```text
Up = 0
```

---

# 12. Filter Average

O preditor é:

```text
floor((Left + Up) / 2)
```

Então:

```text
Filtered = Raw - Average(Left, Up)
```

---

# 13. Filter Paeth

O preditor Paeth utiliza:

```text
Left
Up
UpperLeft
```

para selecionar o valor mais próximo da previsão:

```text
P = Left + Up - UpperLeft
```

O valor entre `Left`, `Up` e `UpperLeft` com menor distância de `P` é utilizado.

---

# 14. RLE

O stream filtrado é codificado utilizando Run-Length Encoding.

O byte de controle possui:

```text
bit 7 = tipo
bits 6-0 = quantidade - 1
```

### Literal

```text
0xxxxxxx
```

A quantidade é:

```text
(control & 0x7F) + 1
```

seguida pelos bytes literais.

### Run

```text
1xxxxxxx
```

A quantidade é:

```text
(control & 0x7F) + 1
```

seguida por um único byte que será repetido.

---

# 15. DEFLATE

Após RLE, o resultado é comprimido utilizando DEFLATE.

A implementação de referência utiliza:

```java
java.util.zip.Deflater
```

na codificação e:

```java
java.util.zip.Inflater
```

na decodificação.

O DEFLATE fornece:

* compressão baseada em LZ77;
* codificação Huffman.

---

# 16. Decoding

O decoder executa:

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

Cada pixel possui três componentes:

```text
R G B
```

Cada componente possui 8 bits.

Portanto:

```text
Pixel = 24 bits
```

Para uma imagem:

```text
Width = W
Height = H
```

a representação RGB não comprimida possui:

```text
W × H × 3 bytes
```

O valor acima representa o tamanho dos pixels antes dos filtros e da compressão.

---

# 18. Endianness

Valores inteiros multi-byte utilizados pelo formato são armazenados em **Big Endian (network byte order)**.

Exemplo:

```text
0x12345678
```

é armazenado como:

```text
12 34 56 78
```

---

# 19. Compatibility

Um decoder deve validar a versão do formato e do codec antes de interpretar os dados.

Arquivos gerados por pipelines incompatíveis não devem ser interpretados como se fossem arquivos compatíveis.

Mudanças incompatíveis devem resultar em uma nova versão de formato ou codec.

---

# 20. Future Extensions

Possíveis extensões:

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

Essas extensões devem ser documentadas antes de serem incorporadas à especificação estável.
