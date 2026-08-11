# 🖼️ ArcImage

> Um formato de imagem experimental, lossless e desenvolvido do zero em Java.

**ArcImage** é um projeto experimental que implementa um formato próprio de imagem, incluindo sua estrutura binária, metadados, compressão e reconstrução dos pixels.

O principal objetivo do projeto é estudar e experimentar técnicas de armazenamento e compressão de imagens, criando uma implementação própria em vez de depender exclusivamente de formatos tradicionais.

> ⚠️ **Status:** experimental. A especificação ainda pode sofrer alterações durante o desenvolvimento.

---

## ✨ Destaques

O ArcImage atualmente utiliza um pipeline de compressão híbrido:

```text
┌──────────────────────┐
│      Imagem RGB      │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│ Filtro adaptativo    │
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
│       Arquivo ARC    │
└──────────────────────┘
```

O processo inverso restaura os dados exatamente:

```text
ARC
 ↓
DEFLATE
 ↓
RLE
 ↓
Filtro inverso
 ↓
RGB original
```

O formato foi projetado para trabalhar com **compressão sem perdas (lossless)**. O objetivo é que a imagem reconstruída possua exatamente os mesmos valores de pixel da imagem original.

---

# 🔐 Compressão Lossless

O ArcImage não utiliza quantização ou redução deliberada de qualidade na versão atual.

O objetivo do codec é:

```text
Imagem original
      ↓
    ARC
      ↓
Imagem reconstruída
```

com:

```text
pixel_original == pixel_reconstruído
```

para todos os pixels.

Portanto, a redução de tamanho não depende de remover detalhes visuais da imagem.

---

# 🧬 Arquitetura do formato

Um arquivo ARC possui uma estrutura semelhante a:

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

O header possui atualmente **12 bytes**.

| Offset | Tamanho | Campo         |
| -----: | ------: | ------------- |
| `0x00` | 4 bytes | Signature     |
| `0x04` |  1 byte | Version Major |
| `0x05` |  1 byte | Version Minor |
| `0x06` | 2 bytes | Width         |
| `0x08` | 2 bytes | Height        |
| `0x0A` |  1 byte | Color format  |
| `0x0B` |  1 byte | Padding       |

### Signature

A assinatura utilizada pelo formato é:

```text
ARCX
```

---

# 📐 Dimensões

Largura e altura são armazenadas como **UInt16 Big Endian**.

Exemplo:

```text
Width:
[HIGH BYTE][LOW BYTE]

Height:
[HIGH BYTE][LOW BYTE]
```

A implementação atual suporta dimensões de até:

```text
65535 × 65535
```

limitadas pela representação UInt16 utilizada pelo formato.

---

# 🎨 Cor

A implementação atual trabalha com:

```text
RGB
```

utilizando:

```text
8 bits Red
8 bits Green
8 bits Blue
```

Total:

```text
24 bits por pixel
```

Alpha/transparência ainda não faz parte do pipeline principal do codec atual.

---

# 🏷️ Metadados

O formato possui uma estrutura de metadados baseada em:

```text
TAG + SIZE + DATA
```

Onde:

```text
TAG  = 4 bytes
SIZE = UInt16
DATA = conteúdo
```

Atualmente são utilizados campos como:

### `AUTH`

Autor responsável pela criação do arquivo.

### `SOFT`

Software utilizado para gerar o arquivo.

### `TIMS`

Timestamp da criação do arquivo.

### `DATA`

Marca o início dos dados comprimidos da imagem.

---

# 🗜️ Pipeline de compressão

## 1. Filtro adaptativo

Cada linha da imagem é analisada utilizando cinco filtros:

```text
0 = None
1 = Sub
2 = Up
3 = Average
4 = Paeth
```

O encoder calcula as cinco possibilidades e seleciona aquela que produz o menor custo para a linha.

Isso transforma padrões espaciais em valores residuais menores e mais repetitivos.

Por exemplo:

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

Após o filtro é aplicado **Run-Length Encoding**.

Sequências repetidas podem ser representadas por:

```text
valor + quantidade
```

Por exemplo:

```text
00 00 00 00 00
```

pode ser representado conceitualmente como:

```text
RUN(5, 00)
```

O RLE atual trabalha sobre os bytes produzidos pelo estágio de filtragem.

---

## 3. DEFLATE

Depois do RLE, os dados são submetidos ao **DEFLATE**.

O DEFLATE combina técnicas de:

* LZ77
* codificação Huffman

A implementação atual utiliza o `Deflater` da biblioteca padrão do Java.

Portanto, o pipeline final é:

```text
Filtro
   ↓
RLE
   ↓
DEFLATE
   ├── LZ77
   └── Huffman
```

---

# 🔄 Decodificação

O decoder executa o processo inverso:

```text
┌──────────────────────┐
│      Arquivo ARC     │
└──────────┬───────────┘
           ↓
      Ler Header
           ↓
       Ler Metadata
           ↓
        DEFLATE
           ↓
          RLE
           ↓
    Restaurar filtros
           ↓
       Restaurar RGB
           ↓
     BufferedImage
```

A imagem reconstruída pode então ser utilizada normalmente através da API de imagem do Java.

---

# 💻 Tecnologias

O projeto atualmente utiliza:

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

Não são necessárias bibliotecas externas para o núcleo atual do codec.

---

# 📂 Estrutura do projeto

A estrutura principal é organizada aproximadamente da seguinte forma:

```text
ArcImage/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── arcanjo/
│                   └── archimage/
│                       ├── codec/
│                       │   └── ArcEncode.java
│                       │
│                       ├── format/
│                       │   └── Format.java
│                       │
│                       └── viewer/
│                           └── ImageView.java
│
├── pom.xml
├── README.md
└── LICENSE
```

---

# 🛠️ Status atual

| Recurso                       | Status                |
| ----------------------------- | --------------------- |
| Estrutura binária própria     | 🟢 Implementado       |
| Header                        | 🟢 Implementado       |
| RGB                           | 🟢 Implementado       |
| Encoder                       | 🟢 Implementado       |
| Decoder                       | 🟢 Implementado       |
| Metadados                     | 🟢 Implementado       |
| Filtros adaptativos           | 🟢 Implementado       |
| RLE                           | 🟢 Implementado       |
| DEFLATE                       | 🟢 Implementado       |
| Compressão lossless           | 🟢 Implementado       |
| Benchmark contra PNG          | 🟡 Em desenvolvimento |
| Alpha / RGBA                  | 🔴 Planejado          |
| Codec independente de Java    | 🔴 Futuro             |
| Ferramentas CLI               | 🔴 Planejado          |
| Visualizador próprio          | 🔴 Futuro             |
| Especificação binária estável | 🟡 Em desenvolvimento |

---

# 🗺️ Roadmap

## ARC 2.x

* [x] Estrutura básica do formato
* [x] Header
* [x] RGB
* [x] Encoder
* [x] Decoder
* [x] Metadados
* [x] Filtros adaptativos
* [x] RLE
* [x] DEFLATE
* [x] Compressão lossless
* [ ] Benchmark automatizado
* [ ] Testes de integridade pixel a pixel
* [ ] Otimização do RLE
* [ ] Escolha adaptativa de estratégias
* [ ] Suporte a imagens RGBA

## ARC 3.x

Possíveis experimentos:

* Preditores adicionais
* Transformação de canais
* RLE orientado a pixels
* Compressão adaptativa por bloco
* Melhor seleção de filtros
* Novos modos de compressão
* Otimização de velocidade
* Paralelização
* Especificação binária estável

---

# 🧪 Benchmark

Os benchmarks do ArcImage devem considerar pelo menos:

```text
Tamanho original
Tamanho PNG
Tamanho ARC
Taxa de compressão
Tempo de compressão
Tempo de descompressão
Integridade dos pixels
```

A integridade deve ser validada comparando a imagem original com a imagem reconstruída pixel a pixel.

Exemplo conceitual:

```java
original.getRGB(x, y)
    ==
decoded.getRGB(x, y)
```

para todas as posições válidas da imagem.

---

# ⚠️ Projeto experimental

O ArcImage ainda está em desenvolvimento.

A especificação binária, os algoritmos de compressão e a estrutura interna do arquivo podem sofrer alterações sem garantia de compatibilidade entre versões.

Arquivos gerados por versões experimentais podem não ser compatíveis com versões futuras do codec.

**Não utilize o formato atual como armazenamento de dados críticos sem manter uma cópia da imagem original.**

---

# 🤝 Contribuições

Contribuições, ideias e experimentos são bem-vindos.

Algumas áreas particularmente interessantes:

* novos filtros de imagem;
* algoritmos de predição;
* RLE adaptativo;
* LZ77;
* Huffman;
* transformações de canais;
* otimização de memória;
* otimização de velocidade;
* testes comparativos com PNG, WebP, AVIF e JPEG XL;
* ferramentas de análise do formato ARC.

---

# 📜 Licença

Este projeto é disponibilizado sob a **MIT License**.

Consulte o arquivo `LICENSE` para os termos completos.

---

# 👨‍💻 Projeto

**ArcImage**

Um formato de imagem experimental construído do zero para estudar:

```text
formatos binários
        +
processamento de imagens
        +
compressão lossless
        +
desenvolvimento de codecs
```

> Construindo um formato de imagem do zero, uma etapa de cada vez.
