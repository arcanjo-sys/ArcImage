# 🖼️ ArcImage

> Um formato de imagem experimental desenvolvido do zero para estudar armazenamento binário, representação de pixels, codificação e decodificação de imagens.

[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://www.java.com/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)
[![Status](https://img.shields.io/badge/status-experimental-yellow)](#status)

## 📖 Sobre o projeto

**ArcImage** é um formato de arquivo de imagem experimental criado para explorar como imagens digitais podem ser representadas, armazenadas e interpretadas em um formato binário próprio.

O projeto não pretende apenas criar uma biblioteca para manipulação de imagens. O objetivo principal é estudar e documentar, desde a base, os conceitos envolvidos na criação de um formato de arquivo.

Entre os conceitos explorados estão:

* estrutura de arquivos binários;
* representação de pixels;
* encoding e decoding;
* metadados;
* versionamento de formatos;
* compressão;
* eficiência de armazenamento.

## 🎯 Objetivos

* [x] Criar a estrutura inicial do formato
* [x] Implementar leitura de pixels
* [x] Implementar escrita de imagens
* [ ] Definir a especificação completa
* [ ] Adicionar suporte a metadados
* [ ] Implementar compressão
* [ ] Criar ferramentas de conversão
* [ ] Desenvolver visualizadores
* [ ] Estabilizar a especificação

## 🧬 Estrutura do formato

O arquivo ArcImage é organizado conceitualmente da seguinte maneira:

```text
┌──────────────────────────────┐
│            HEADER            │
├──────────────────────────────┤
│       IMAGE METADATA         │
├──────────────────────────────┤
│          PIXEL DATA          │
├──────────────────────────────┤
│        OPTIONAL DATA         │
└──────────────────────────────┘
```

### Header

O cabeçalho contém as informações necessárias para interpretar o arquivo, incluindo dados como:

* identificação do formato;
* versão;
* largura;
* altura;
* profundidade de cor;
* formato de armazenamento dos pixels.

Para conhecer a estrutura binária detalhada, consulte a [especificação do ArcImage](docs/SPECIFICATION.md).

## 🚀 Começando

### Requisitos

* Java 17 ou superior
* Maven

### Clonar o projeto

```bash
git clone https://github.com/arcanjo-sys/ArcImage.git
cd ArcImage
```

### Compilar

```bash
mvn clean package
```

### Executar os testes

```bash
mvn test
```

## 🏗️ Arquitetura

A implementação está organizada em componentes responsáveis por diferentes partes do processamento do formato.

Consulte [Architecture](docs/ARCHITECTURE.md) para conhecer a organização interna do projeto.

## 📚 Documentação

| Documento                              | Descrição                          |
| -------------------------------------- | ---------------------------------- |
| [Especificação](docs/SPECIFICATION.md) | Especificação do formato ArcImage  |
| [Formato binário](docs/FILE_FORMAT.md) | Estrutura byte a byte do arquivo   |
| [Arquitetura](docs/ARCHITECTURE.md)    | Organização interna do projeto     |
| [Desenvolvimento](docs/DEVELOPMENT.md) | Guia para desenvolver e contribuir |
| [Roadmap](docs/ROADMAP.md)             | Próximos objetivos do projeto      |

## ⚠️ Status

O ArcImage é um projeto **experimental**.

A especificação ainda está em desenvolvimento e pode sofrer alterações incompatíveis entre versões.

Portanto, arquivos ArcImage produzidos atualmente não devem ser considerados necessariamente compatíveis com versões futuras.

## 🗺️ Roadmap

### Formato

* [x] Estrutura inicial
* [x] Representação básica de pixels
* [x] Decoder
* [x] Encoder
* [ ] Especificação formal
* [ ] Metadados
* [ ] Compressão

### Ferramentas

* [x] CLI
* [ ] Conversor de imagens
* [ ] Visualizador
* [ ] Ferramentas de inspeção do formato

### Ecossistema

* [ ] Documentação completa
* [ ] Implementação de referência
* [ ] Suporte em outras linguagens
* [ ] Especificação estável

## 🤝 Contribuindo

Contribuições, ideias e sugestões são bem-vindas.

Antes de abrir uma issue ou pull request, consulte o [guia de contribuição](CONTRIBUTING.md).

## 📜 Licença

Este projeto está disponível sob a licença MIT.

Consulte o arquivo [LICENSE](LICENSE) para obter os termos completos.

---

> Um novo formato de imagem, construído do zero.
