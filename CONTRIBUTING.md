# Contributing to ArcImage

Obrigado pelo interesse em contribuir com o **ArcImage**!

O ArcImage é um projeto experimental focado no desenvolvimento e estudo de um formato de imagem próprio. Contribuições são bem-vindas, especialmente aquelas relacionadas à implementação, especificação, testes e documentação.

---

## 1. Antes de contribuir

Antes de começar, recomendamos:

* ler o [README](README.md);
* consultar a [especificação do formato](docs/SPECIFICATION.md);
* consultar a [estrutura do formato](docs/FILE_FORMAT.md);
* consultar a [arquitetura](docs/ARCHITECTURE.md);
* verificar o [roadmap](docs/ROADMAP.md).

Se você pretende alterar o formato binário, leia principalmente a especificação antes de começar.

---

## 2. Tipos de contribuição

Você pode contribuir de diversas formas:

### Código

* implementar novos recursos;
* corrigir bugs;
* melhorar o Encoder;
* melhorar o Decoder;
* melhorar o tratamento de erros;
* melhorar desempenho;
* refatorar código.

### Especificação

* corrigir ambiguidades;
* melhorar a definição dos campos;
* propor novos chunks;
* documentar comportamentos;
* definir regras de compatibilidade.

### Testes

* adicionar testes unitários;
* adicionar testes de integração;
* criar arquivos ArcImage de teste;
* testar arquivos inválidos;
* testar compatibilidade entre versões.

### Documentação

* melhorar o README;
* corrigir documentação;
* adicionar exemplos;
* melhorar explicações técnicas;
* documentar decisões de arquitetura.

---

## 3. Encontrando uma issue

Antes de implementar uma alteração grande, verifique se já existe uma issue relacionada.

Caso não exista, considere abrir uma issue descrevendo:

* o problema;
* o comportamento esperado;
* o comportamento atual;
* uma possível solução, caso tenha uma.

Para mudanças grandes no formato, é recomendado discutir a proposta antes da implementação.

---

## 4. Configurando o ambiente

Clone o repositório:

```bash
git clone https://github.com/arcanjo-sys/ArcImage.git
cd ArcImage
```

Compile o projeto:

```bash
mvn clean package
```

Execute os testes:

```bash
mvn test
```

Consulte o [Development Guide](docs/DEVELOPMENT.md) para informações adicionais.

---

## 5. Criando uma branch

Evite trabalhar diretamente na branch principal.

Crie uma branch específica para sua alteração:

```bash
git checkout -b feature/nome-da-feature
```

Para correções:

```bash
git checkout -b fix/nome-do-problema
```

Para documentação:

```bash
git checkout -b docs/nome-da-documentacao
```

---

## 6. Commits

Prefira commits pequenos, objetivos e relacionados a uma única alteração.

Exemplos:

```text
feat: add AUTH metadata chunk
fix: validate image dimensions
docs: update file format specification
test: add decoder tests
refactor: simplify pixel reader
```

Evite commits genéricos como:

```text
update
changes
fix
stuff
```

---

## 7. Alterações no formato

Alterações no formato binário exigem atenção especial.

Antes de modificar a estrutura:

1. Atualize a especificação.
2. Documente a alteração.
3. Avalie a compatibilidade.
4. Atualize o Encoder.
5. Atualize o Decoder.
6. Adicione ou atualize os testes.
7. Atualize os arquivos de exemplo, quando necessário.

Alterações incompatíveis devem resultar em uma nova versão do formato.

Consulte [Versioning](SPECIFICATION.md#9-versioning).

---

## 8. Adicionando novos chunks

Novos chunks devem seguir a estrutura:

```text
┌──────────────┬──────────────┬─────────────────┐
│    Marker    │    Length    │      Data       │
│   4 bytes    │   2 bytes    │    variável     │
└──────────────┴──────────────┴─────────────────┘
```

O Marker deve possuir exatamente 4 bytes.

A contribuição deve documentar:

* nome do chunk;
* finalidade;
* tamanho;
* tipo;
* encoding;
* regras de validação;
* compatibilidade.

Exemplo:

```text
EXIF
```

---

## 9. Testes

Toda nova funcionalidade deve, quando aplicável, possuir testes.

Execute:

```bash
mvn test
```

Antes de abrir um Pull Request, certifique-se de que os testes existentes continuam passando.

Para alterações no formato, considere adicionar arquivos de teste para:

* arquivos válidos;
* arquivos inválidos;
* Header inválido;
* versão não suportada;
* chunks inválidos;
* arquivos truncados;
* diferentes dimensões;
* diferentes profundidades de cor.

---

## 10. Pull Requests

Um Pull Request deve explicar claramente:

* o que foi alterado;
* por que a alteração foi necessária;
* como a alteração funciona;
* quais testes foram realizados;
* se houve alteração na especificação;
* se existe impacto na compatibilidade.

Exemplo:

```text
## Descrição

Adiciona suporte ao chunk AUTH.

## Alterações

- adiciona leitura do chunk AUTH;
- adiciona escrita do chunk AUTH;
- atualiza a especificação;
- adiciona testes.

## Testes

mvn test

## Compatibilidade

Compatível com a versão atual do formato.
```

---

## 11. Revisão

Pull Requests podem passar por revisão antes de serem aceitos.

Durante a revisão podem ser solicitadas alterações relacionadas a:

* qualidade do código;
* testes;
* documentação;
* compatibilidade;
* arquitetura;
* consistência com a especificação.

O objetivo da revisão é manter o projeto consistente e sustentável.

---

## 12. Estilo de código

Procure manter o código:

* simples;
* legível;
* consistente;
* modular;
* documentado quando necessário.

Evite adicionar complexidade sem uma necessidade clara.

---

## 13. Documentação

Se uma alteração modificar o comportamento do ArcImage, a documentação correspondente também deve ser atualizada.

Por exemplo:

```text
Alteração no formato
        │
        ├── SPECIFICATION.md
        ├── FILE_FORMAT.md
        ├── testes
        └── implementação
```

A implementação e a especificação devem permanecer consistentes.

---

## 14. Código de conduta

Contribuidores devem manter uma comunicação respeitosa e colaborativa.

Não serão tolerados:

* assédio;
* ataques pessoais;
* discriminação;
* comportamento intimidatório;
* contribuições deliberadamente maliciosas.

---

## 15. Licença

Ao contribuir com o ArcImage, você concorda que suas contribuições poderão ser distribuídas sob a mesma licença utilizada pelo projeto.

Consulte o arquivo [LICENSE](./LICENSE) para obter os termos da licença.

---

## 16. Obrigado

Toda contribuição é bem-vinda, desde uma correção simples de documentação até uma nova implementação.

Obrigado por ajudar a desenvolver o ArcImage!
