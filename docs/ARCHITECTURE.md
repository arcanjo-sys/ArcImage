# ArcImage Architecture

## 1. Visão geral

O ArcImage é organizado em componentes responsáveis pela criação, leitura e interpretação de arquivos no formato ArcImage.

A arquitetura busca manter uma separação clara entre:

* representação da imagem;
* codificação;
* decodificação;
* estrutura do arquivo;
* metadados;
* armazenamento de pixels.

---

# 2. Fluxo geral

## Encoding

O processo de criação de um arquivo ArcImage segue, conceitualmente:

```text
Image
  │
  ▼
Image Data
  │
  ├──────────────► Metadata
  │
  └──────────────► Pixel Data
                        │
                        ▼
                  ArcImage Encoder
                        │
                        ▼
                  ArcImage File
```

## Decoding

O processo inverso:

```text
ArcImage File
      │
      ▼
ArcImage Decoder
      │
      ├──────────────► Header
      │
      ├──────────────► Metadata
      │
      └──────────────► Pixel Data
                            │
                            ▼
                         Image
```

---

# 3. Componentes

## 3.1 Encoder

O Encoder é responsável por transformar uma representação de imagem em um arquivo ArcImage.

Suas principais responsabilidades são:

* criar o Header;
* escrever metadados;
* escrever as dimensões;
* escrever a profundidade de cor;
* escrever os pixels usando o SARCcA (Spatial ARC Compression Algorithm);
* produzir o arquivo binário final.

---

## 3.2 Decoder

O Decoder realiza o processo inverso.

Suas responsabilidades incluem:

* validar o Magic;
* identificar a versão;
* ler as dimensões;
* identificar a profundidade de cor;
* ler os chunks;
* interpretar os pixels;
* reconstruir a imagem.

---

## 3.3 Viewer

O Viewer interpreta a imagem e mostra os pixels na tela.

Suas responsabilidades incluem:

* validar o Magic;
* identificar a versão;
* ler as dimensões;
* identificar a profundidade de cor;
* ler os chunks;
* interpretar os pixels;
* mostrar a imagem.

---

# 4. Header

O Header é a primeira estrutura interpretada pelo Decoder.

```text
┌──────────────────────────┐
│ Magic                    │
├──────────────────────────┤
│ Version                  │
├──────────────────────────┤
│ Width                    │
├──────────────────────────┤
│ Height                   │
├──────────────────────────┤
│ Color Depth              │
├──────────────────────────┤
│ Padding                  │
└──────────────────────────┘
```

O Header permite determinar como o restante do arquivo deve ser interpretado.

---

# 5. Pixel representation

Os pixels são armazenados de acordo com o `Color Depth`.

A implementação deve manter a representação interna da imagem separada da representação binária sempre que possível.

Isso permite alterar a forma como os pixels são armazenados sem necessariamente modificar a lógica de manipulação da imagem.

---

# 6. Metadata

Os metadados são representados por chunks.

Cada chunk contém:

```text
Marker
Length
Data
```

A arquitetura deve permitir adicionar novos tipos de chunks sem exigir alterações significativas no mecanismo principal de leitura.

Chunks atualmente definidos:

```text
AUTH
TIMS
SOFT
COPY
```

---

# 7. Error handling

O Decoder e o Viewer deve detectar situações como:

* Magic inválido;
* versão não suportada;
* Header incompleto;
* dimensões inválidas;
* dados de pixel insuficientes;
* chunk incompleto;
* Length inconsistente;
* arquivo truncado.

Erros de formato devem ser tratados separadamente de erros de I/O sempre que possível.

---

# 8. Versionamento

A versão armazenada no Header determina como o arquivo deve ser interpretado.

```text
Version
   │
   ▼
Decoder
   │
   ├── versão suportada
   │        │
   │        ▼
   │     decode
   │
   └── versão não suportada
            │
            ▼
          error
```

---

# 9. Extensibilidade

A arquitetura deve permitir a inclusão futura de:

* compressão;
* novos formatos de pixel;
* novos chunks;
* thumbnails;
* perfis de cor;
* checksums;
* ferramentas de conversão.

Novos recursos devem preferencialmente ser adicionados de forma compatível com arquivos existentes.

---

# 10. Princípios

O projeto segue alguns princípios:

### Simplicidade

O formato deve ser simples o suficiente para ser implementado e analisado manualmente.

### Determinismo

Uma mesma estrutura de imagem deve produzir uma estrutura ArcImage previsível.

### Extensibilidade

O formato deve permitir evolução sem exigir uma reconstrução completa da especificação.

### Portabilidade

A especificação deve evitar depender de detalhes específicos da linguagem Java.

### Documentação

O comportamento do formato deve estar documentado independentemente da implementação de referência.
