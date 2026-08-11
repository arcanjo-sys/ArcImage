# ArcImage Development Guide

## 1. Sobre este documento

Este documento descreve como configurar o ambiente de desenvolvimento do ArcImage e como trabalhar no projeto.

O objetivo é facilitar a contribuição e manter o processo de desenvolvimento consistente.

---

# 2. Requisitos

Antes de começar, instale:

* Java;
* Maven;
* Git.

A versão mínima do Java deve ser verificada no `pom.xml` do projeto.

---

# 3. Obtendo o código

Clone o repositório:

```bash
git clone https://github.com/arcanjo-sys/ArcImage.git
```

Entre no diretório:

```bash
cd ArcImage
```

---

# 4. Compilação

Para compilar o projeto:

```bash
mvn clean package
```

Para apenas compilar:

```bash
mvn compile
```

---

# 5. Testes

Execute os testes com:

```bash
mvn test
```

Os testes devem verificar principalmente:

* criação de arquivos;
* leitura de arquivos;
* valores do Header;
* dimensões;
* profundidade de cor;
* leitura e escrita de pixels;
* chunks;
* arquivos inválidos.

---

# 6. Estrutura do projeto

A estrutura pode ser organizada da seguinte maneira:

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

A estrutura real dos pacotes Java deve ser mantida como referência principal para esta seção.

---

# 7. Desenvolvimento do formato

Alterações na estrutura binária devem ser tratadas com cuidado.

Antes de modificar o formato:

1. Atualize a especificação.
2. Defina o impacto da alteração.
3. Atualize o Encoder.
4. Atualize o Decoder.
5. Atualize o Viewer
6. Adicione ou atualize os testes.
7. Verifique compatibilidade.
8. Atualize a documentação.

---

# 8. Adicionando um novo chunk

Um novo chunk deve possuir:

```text
┌──────────────┬──────────────┬─────────────────┐
│    Marker    │    Length    │      Data       │
│   4 bytes    │   2 bytes    │    variável     │
└──────────────┴──────────────┴─────────────────┘
```

O Marker deve possuir exatamente 4 bytes.

Exemplo:

```text
EXIF
```

Antes de adicionar um novo chunk, documente:

* nome;
* finalidade;
* tamanho;
* tipo;
* encoding;
* regras de validação;
* compatibilidade.

---

# 9. Compatibilidade

Mudanças incompatíveis com versões anteriores devem resultar em uma nova versão do formato.

Exemplo:

```text
Version 1
    │
    ├── mudanças compatíveis
    │
    ▼
Version 1

    │
    └── mudanças incompatíveis
            │
            ▼
        Version 2
```

---

# 10. Pull Requests

Pull Requests devem explicar:

* o que foi alterado;
* por que foi alterado;
* quais arquivos foram modificados;
* quais testes foram executados;
* se houve alteração no formato binário.

Alterações no formato devem incluir atualização da documentação correspondente.

---

# 11. Commits

Prefira commits pequenos e objetivos.

Exemplos:

```text
feat: add AUTH metadata chunk
fix: validate image dimensions
docs: update file format specification
test: add decoder tests
refactor: simplify pixel reader
```

---

# 12. Debugging

Durante o desenvolvimento de um formato binário, é útil inspecionar os arquivos diretamente.

Exemplo:

```bash
xxd image.arc
```

ou:

```bash
hexdump -C image.arc
```

Isso permite comparar os bytes produzidos pelo Encoder com a especificação.

---

# 13. Testes de compatibilidade

Arquivos de teste devem ser mantidos para versões relevantes do formato.

Exemplo:

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

Esses arquivos ajudam a evitar regressões na implementação.

---

# 14. Checklist

Antes de enviar uma alteração:

* [ ] O projeto compila.
* [ ] Os testes passam.
* [ ] Novos comportamentos possuem testes.
* [ ] A especificação foi atualizada.
* [ ] A documentação foi atualizada.
* [ ] Compatibilidade foi verificada.
* [ ] O código não depende de detalhes não documentados do formato.
