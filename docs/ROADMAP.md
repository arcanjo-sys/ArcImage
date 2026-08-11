# ArcImage Roadmap

## Visão

O objetivo do ArcImage é evoluir de um projeto experimental para uma especificação de formato de imagem bem definida, documentada e implementável.

Este roadmap representa a direção planejada do projeto e pode ser alterado conforme o desenvolvimento.

---

# Phase 1 — Core Format

### Header

* [x] Magic
* [x] Version
* [x] Width
* [x] Height
* [x] Color Depth
* [x] Padding

### Pixel Data

* [x] Representação básica dos pixels
* [ ] Definir formalmente todos os formatos de pixel suportados
* [ ] Documentar ordem dos canais
* [ ] Documentar limites de cada formato

### Encoder

* [x] Implementação inicial
* [ ] Validação completa do Header
* [ ] Validação dos pixels
* [ ] Testes automatizados

### Decoder

* [x] Implementação inicial
* [ ] Validação do Magic
* [ ] Validação da versão
* [ ] Tratamento de arquivos truncados
* [ ] Testes automatizados

---

# Phase 2 — Metadata

### Chunks

* [x] AUTH
* [x] TIMS
* [x] SOFT
* [x] COPY
* [ ] Definir regras formais de ordenação
* [ ] Definir comportamento para chunks desconhecidos
* [ ] Definir limite máximo de chunks
* [ ] Definir encoding oficial

---

# Phase 3 — Specification

* [ ] Finalizar `SPECIFICATION.md`
* [ ] Finalizar `FILE_FORMAT.md`
* [ ] Definir Endianness
* [ ] Definir todos os valores válidos de Color Depth
* [ ] Definir estrutura definitiva dos Pixel Data
* [ ] Definir estrutura definitiva dos chunks
* [ ] Definir regras de validação
* [ ] Definir comportamento de erros
* [ ] Criar exemplos binários
* [ ] Criar arquivos de referência

---

# Phase 4 — Compression

* [x] Definir necessidade de compressão
* [ ] Avaliar algoritmos
* [ ] Definir formato dos dados comprimidos
* [ ] Adicionar campo ou chunk de compressão
* [ ] Implementar encoder
* [ ] Implementar decoder
* [ ] Criar testes de compressão
* [ ] Comparar tamanho dos arquivos

---

# Phase 5 — Integrity

* [ ] Avaliar checksum
* [ ] Avaliar CRC
* [ ] Avaliar hash
* [ ] Definir mecanismo oficial
* [ ] Implementar validação
* [ ] Implementar geração
* [ ] Testar arquivos corrompidos

---

# Phase 6 — Tooling

Criar ferramentas para facilitar o uso e desenvolvimento do formato.

* [X] CLI
* [ ] Conversor PNG → ArcImage
* [ ] Conversor JPEG → ArcImage
* [ ] Conversor ArcImage → PNG
* [ ] Inspector de arquivos
* [ ] Visualizador
* [ ] Gerador de arquivos de teste

---

# Phase 7 — Ecosystem

* [ ] API Java estável
* [ ] Documentação da API
* [ ] Implementação em outra linguagem
* [ ] Exemplos de implementação
* [ ] Test suite compartilhada
* [ ] Arquivos de referência
* [ ] Especificação independente da implementação Java

---

# Phase 8 — Stable Format

Antes de declarar uma versão estável:

* [ ] Especificação completa
* [ ] Encoder de referência
* [ ] Decoder de referência
* [ ] Testes abrangentes
* [ ] Arquivos de teste públicos
* [ ] Compatibilidade documentada
* [ ] Versionamento definido
* [ ] Política de breaking changes
* [ ] Documentação revisada

---

# Long-term Goals

No futuro, o ArcImage poderá oferecer suporte a:

* diferentes espaços de cor;
* diferentes profundidades de cor;
* transparência;
* compressão;
* thumbnails;
* perfis de cor;
* metadados avançados;
* animações;
* múltiplas imagens por arquivo;
* extensões específicas de aplicação.

Esses recursos só devem ser adicionados quando houver uma necessidade clara e uma especificação suficientemente definida.

---

# Status

O ArcImage permanece em desenvolvimento experimental.

Itens deste roadmap não representam necessariamente compromissos de implementação ou datas de lançamento.
