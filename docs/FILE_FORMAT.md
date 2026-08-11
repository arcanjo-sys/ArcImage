# ArcImage File Format

## 1. Visão geral

Um arquivo **ArcImage** é um arquivo binário estruturado em diferentes seções.

A estrutura básica é composta por:

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

A ordem e a presença das seções opcionais dependem da versão do formato.

---

## 2. Header

O Header contém as informações necessárias para que um decodificador consiga identificar e interpretar o arquivo.

### Estrutura

| Campo       | Tamanho | Tipo   | Descrição                 |
| ----------- | ------: | ------ | ------------------------- |
| Magic       | 4 bytes | ASCII  | Identificador do formato  |
| Version     | 2 bytes | uint16 | Versão do formato         |
| Width       | 2 bytes | uint16 | Largura da imagem         |
| Height      | 2 bytes | uint16 | Altura da imagem          |
| Color Depth |  1 byte | uint8  | Profundidade de cor       |
| Padding     |  1 byte | uint8  | Reservado para uso futuro |

Total:

```text
12 bytes
```

### Magic

Os primeiros 4 bytes do arquivo identificam o formato como ArcImage.

O valor utilizado é:

```text
ARCX
```



### Version

Representa a versão da especificação utilizada para criar o arquivo.

O campo possui 2 bytes:

```text
uint16
```

Exemplo:

```text
0x0001
```

representa a versão `1`.

### Width

Largura da imagem em pixels.

Tipo:

```text
uint16
```

O valor máximo representável é:

```text
65535 pixels
```

### Height

Altura da imagem em pixels.

Tipo:

```text
uint16
```

O valor máximo representável é:

```text
65535 pixels
```

### Color Depth

Indica a quantidade de bits utilizada para representar a informação de cor de cada pixel.

Exemplos possíveis:

```text
03
```

Os valores válidos devem ser definidos pela versão correspondente da especificação.

### Padding

Byte reservado para futuras extensões do formato.

Em versões atuais, esse campo deve ser escrito como:

```text
0x00
```

Um decodificador deve ignorar o valor desse campo caso a especificação da versão não defina outro significado.

---

# 3. Metadata

Os metadados são armazenados utilizando estruturas chamadas **chunks**.

Cada chunk possui uma estrutura geral:

```text
┌──────────────┬──────────────┬─────────────────────┐
│    Marker    │    Length    │        Data         │
│   4 bytes    │   2 bytes    │     variável        │
└──────────────┴──────────────┴─────────────────────┘
```

Onde:

* `Marker` identifica o tipo do chunk;
* `Length` informa o tamanho do conteúdo;
* `Data` contém a informação armazenada.

---

## 3.1 AUTH

Armazena o autor da imagem.

```text
┌──────────┬──────────┬────────────────┐
│   AUTH   │  Length  │      Text      │
│ 4 bytes  │ 2 bytes  │    variável    │
└──────────┴──────────┴────────────────┘
```

| Campo  |  Tamanho | Tipo   |
| ------ | -------: | ------ |
| AUTH   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variável | ASCII  |

Exemplo conceitual:

```text
AUTH
0005
Alice
```

---

## 3.2 TIMS

Armazena a informação temporal associada à imagem.

```text
┌──────────┬──────────┬──────────────┐
│   TIMS   │  Length  │     Text     │
│ 4 bytes  │ 2 bytes  │   4 bytes    │
└──────────┴──────────┴──────────────┘
```

| Campo  |  Tamanho | Tipo   |
| ------ |---------:| ------ |
| TIMS   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variável | ASCII  |

O significado exato do conteúdo de `TIMS` deve ser definido pela especificação da versão.

---

## 3.3 SOFT

Identifica o software responsável pela geração da imagem.

```text
┌──────────┬──────────┬────────────────┐
│   SOFT   │  Length  │      Text      │
│ 4 bytes  │ 2 bytes  │    variável    │
└──────────┴──────────┴────────────────┘
```

| Campo  |  Tamanho | Tipo   |
| ------ | -------: | ------ |
| SOFT   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variável | ASCII  |

Exemplo:

```text
SOFT
0007
ArcImage
```

---

## 3.4 COPY

Armazena informações de copyright.

```text
┌──────────┬──────────┬────────────────┐
│   COPY   │  Length  │      Text      │
│ 4 bytes  │ 2 bytes  │    variável    │
└──────────┴──────────┴────────────────┘
```

| Campo  |  Tamanho | Tipo   |
| ------ | -------: | ------ |
| COPY   |  4 bytes | ASCII  |
| Length |  2 bytes | uint16 |
| Text   | variável | ASCII  |

Exemplo:

```text
COPY
0011
Copyright 2026
```

---

# 4. Pixel Data

Após o Metadata, o arquivo pode conter os dados dos pixels.

A representação dos pixels depende do campo `Color Depth`.

Por exemplo, para uma imagem RGB de 24 bits:

```text
Pixel 0:
R G B

Pixel 1:
R G B

Pixel 2:
R G B
```

Uma imagem com largura `W` e altura `H` possui:

```text
W × H
```

pixels.

Para uma representação RGB de 24 bits:

```text
Pixel Data Size = Width × Height × 3
```

---

# 5. Optional Data

Dados opcionais são informações que não são necessárias para representar os pixels da imagem.

Exemplos futuros:

* thumbnails;
* informações de câmera;
* perfis de cor;
* informações de edição;
* dados de aplicação;
* extensões específicas.

Esses dados devem utilizar estruturas identificáveis e não devem impedir que um decodificador básico consiga ler os dados essenciais da imagem.

---

# 6. Ordem dos dados

Uma implementação pode organizar o arquivo seguindo:

```text
HEADER
METADATA
PIXEL DATA
OPTIONAL DATA
```

Exemplo:

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

A ordem definitiva deve ser considerada parte da especificação quando o formato atingir uma versão estável.

---

# 7. Integridade

Uma futura versão do ArcImage poderá adicionar mecanismos de integridade, como:

* checksum;
* CRC;
* hash;
* detecção de arquivos incompletos ou corrompidos.

Esses mecanismos ainda não fazem parte da estrutura básica definida atualmente.

---

# 8. Extensibilidade

Novos chunks poderão ser adicionados sem necessariamente alterar o Header.

Um leitor que encontrar um chunk desconhecido deve, quando possível, ignorá-lo utilizando o campo `Length` para determinar onde o próximo chunk começa.

Isso permite que versões futuras adicionem funcionalidades sem quebrar completamente implementações antigas.
