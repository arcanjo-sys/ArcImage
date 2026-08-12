# ArcImage Architecture

## 1. Visão geral

O ArcImage é um formato de imagem experimental com foco em **compressão lossless**, representação binária própria e implementação independente da estrutura interna de imagens do Java.

A arquitetura atual é composta por:

* representação da imagem;
* filtros preditivos;
* RLE;
* DEFLATE;
* encoder;
* decoder;
* viewer;
* metadados;
* estrutura binária do arquivo.

A implementação atual trabalha com imagens **RGB de 24 bits por pixel**.

---

## 2. Pipeline de encoding

A versão atual utiliza o seguinte pipeline:

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

O processo é lossless.

Nenhuma informação de pixel é descartada durante o processo de compressão.

---

## 3. Pipeline de decoding

O decoder executa o processo inverso:

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

Antes da compressão, cada linha da imagem é analisada utilizando cinco filtros.

```text
0 = None
1 = Sub
2 = Up
3 = Average
4 = Paeth
```

Para cada linha, o encoder gera as cinco representações e calcula um custo para cada uma.

A representação com menor custo é selecionada.

### None

Não aplica predição.

```text
F(x) = Raw(x)
```

### Sub

Prediz o pixel utilizando o valor à esquerda.

```text
F(x) = Raw(x) - Left(x)
```

### Up

Prediz o pixel utilizando o valor da linha anterior.

```text
F(x) = Raw(x) - Up(x)
```

### Average

Utiliza a média entre os valores à esquerda e acima.

```text
F(x) = Raw(x) - floor((Left(x) + Up(x)) / 2)
```

### Paeth

Utiliza o preditor Paeth baseado em:

```text
Left
Up
UpperLeft
```

Esses filtros transformam dados de imagem em resíduos mais previsíveis.

---

## 5. RLE

Após a filtragem, o resultado passa por Run-Length Encoding.

O RLE representa sequências repetidas de bytes de maneira compacta.

O encoder evita utilizar RLE quando uma repetição não proporciona vantagem.

Estruturalmente:

```text
Control Byte
     │
     ├── Literal
     │
     └── Run
```

O decoder restaura os bytes originais antes da etapa de filtragem inversa.

---

## 6. DEFLATE

Após RLE, os dados são processados pelo algoritmo DEFLATE.

A implementação atual utiliza:

```java
java.util.zip.Deflater
```

e o decoder utiliza:

```java
java.util.zip.Inflater
```

O DEFLATE combina técnicas de:

* LZ77;
* codificação Huffman.

Assim, a arquitetura completa combina três níveis de compressão:

```text
Spatial Prediction
        +
Run-Length Encoding
        +
LZ77/Huffman
```

---

## 7. Encoder

O encoder é responsável por:

1. carregar a imagem;
2. validar dimensões;
3. converter os pixels para RGB;
4. gerar filtros adaptativos;
5. aplicar RLE;
6. aplicar DEFLATE;
7. construir o header;
8. escrever metadados;
9. escrever o bloco `DATA`;
10. gerar o arquivo `.arc`.

---

## 8. Decoder

O decoder é responsável por:

1. validar a assinatura;
2. ler dimensões;
3. identificar o codec;
4. ler metadados;
5. ler o bloco comprimido;
6. executar DEFLATE;
7. executar RLE inverso;
8. restaurar os filtros;
9. reconstruir os pixels RGB;
10. produzir a imagem final.

---

## 9. Viewer

O Viewer utiliza o decoder para reconstruir a imagem e apresentá-la ao usuário.

O Viewer não deve implementar uma segunda lógica de decodificação.

A arquitetura recomendada é:

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

Isso evita divergências entre o comportamento do decoder e do visualizador.

---

## 10. File Structure

A estrutura lógica atual é:

```text
HEADER
METADATA
DATA
    CODEC VERSION
    COMPRESSED SIZE
    COMPRESSED DATA
```

O `DATA` marca o início da representação comprimida.

---

## 11. Metadata

Os metadados utilizam chunks:

```text
┌────────────┬────────────┬──────────────┐
│   Marker   │   Length   │     Data     │
│  4 bytes   │  2 bytes   │   variável   │
└────────────┴────────────┴──────────────┘
```

Chunks atualmente utilizados pela implementação incluem:

```text
AUTH
SOFT
TIMS
```

Novos chunks podem ser adicionados no futuro.

---

## 12. Error Handling

O decoder deve rejeitar arquivos com:

* assinatura inválida;
* dimensões inválidas;
* codec desconhecido;
* dados comprimidos truncados;
* RLE inválido;
* filtros inválidos;
* tamanho inconsistente;
* dados de imagem incompletos.

Erros de formato devem ser tratados separadamente de erros de I/O sempre que possível.

---

## 13. Lossless Guarantee

A propriedade fundamental do codec é:

```text
Original RGB
     ==
Decoded RGB
```

para todos os pixels.

A validação recomendada é uma comparação pixel a pixel:

```java
original.getRGB(x, y) == decoded.getRGB(x, y)
```

para toda a área da imagem.

---

## 14. Extensibilidade

A arquitetura foi projetada para permitir futuras extensões:

* RGBA;
* novos filtros;
* novos algoritmos de compressão;
* transformação de canais;
* checksums;
* perfis de cor;
* thumbnails;
* streaming;
* compressão por blocos.

Extensões incompatíveis devem ser identificadas através do versionamento do formato ou do codec.

---

## 15. Princípios

### Lossless

Nenhum pixel deve ser perdido durante a compressão.

### Simplicidade

A estrutura deve permanecer compreensível e analisável.

### Extensibilidade

O formato deve permitir novas funcionalidades sem comprometer sua estrutura básica.

### Portabilidade

A especificação deve ser independente da implementação Java.

### Determinismo

O processo de encoding deve possuir comportamento previsível para uma determinada entrada e configuração.

### Documentação

A especificação deve ser suficiente para permitir implementações independentes do codec.
