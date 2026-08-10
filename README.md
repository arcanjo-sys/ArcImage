# 🖼️ ArcImage

> Um novo formato de imagem experimental desenvolvido para fins de estudo.

## 📖 Sobre

**ArcImage** é um formato de arquivo de imagem experimental criado para explorar uma abordagem diferente para armazenamento e representação de imagens digitais.

O projeto busca desenvolver uma especificação própria para imagens, definindo como os dados de uma imagem são organizados, armazenados e interpretados.

A ideia é construir o formato desde a base, incluindo sua estrutura binária, representação de pixels e posteriormente recursos como compressão e metadados.

## 🎯 Objetivos

* 📦 Criar uma estrutura própria de arquivo de imagem
* 🎨 Armazenar dados de pixels de forma eficiente
* ⚡ Buscar uma implementação simples e rápida
* 🔬 Explorar conceitos de formatos binários
* 🧩 Desenvolver uma especificação aberta e documentada
* 🚀 Experimentar possíveis técnicas de compressão e otimização

## 🧬 Estrutura do formato

O formato utiliza uma estrutura binária própria para representar os dados da imagem.

A especificação está sendo desenvolvida e pode sofrer alterações durante o desenvolvimento.

Exemplo conceitual:

```text
┌──────────────────────────────┐
│           HEADER             │
├──────────────────────────────┤
│       IMAGE METADATA         │
├──────────────────────────────┤
│        PIXEL DATA            │
├──────────────────────────────┤
│      OPTIONAL DATA           │
└──────────────────────────────┘
```

### Header

O cabeçalho contém informações necessárias para interpretar o arquivo, como:

* Identificação do formato
* Versão
* Largura
* Altura
* Profundidade de cor
* Tipo de armazenamento dos pixels

### Pixel Data

A seção de dados contém os pixels da imagem de acordo com a representação definida pela especificação.

## 🛠️ Status

> ⚠️ **Projeto experimental**

O formato ainda está em desenvolvimento e sua especificação pode mudar.

| Recurso                        | Status                |
| ------------------------------ | --------------------- |
| Estrutura básica do arquivo    | 🟢 Implementado       |
| Leitura de pixels              | 🟢 Implementado       |
| Escrita de imagens             | 🟢 Implementado       |
| Metadados                      | 🟡 Em Desenvolvimento |
| Compressão                     | 🟡 Planejado          |
| Ferramentas de conversão       | 🔴 Planejado          |
| Suporte em aplicações externas | 🔴 Futuro             |

## 💻 Desenvolvimento

O projeto está sendo desenvolvido utilizando:

* **Java**
* **IntelliJ IDEA**
* **Git / GitHub**

## 🚀 Roadmap

* [x] Definir estrutura inicial do arquivo
* [x] Definir representação básica dos pixels
* [x] Implementar decoder
* [x] Implementar encoder
* [ ] Finalizar especificação do formato
* [ ] Adicionar metadados
* [ ] Desenvolver compressão
* [ ] Criar ferramentas de conversão
* [ ] Criar documentação completa da especificação
* [ ] Desenvolver suporte para visualizadores de imagem

## 📚 Especificação

A documentação técnica do formato será disponibilizada neste repositório à medida que a especificação evoluir.

> A especificação atual deve ser considerada experimental e não está garantida como estável.

## 🤝 Contribuições

O projeto está em desenvolvimento e sugestões são bem-vindas.

Antes de contribuir, consulte a documentação e a especificação atual do formato.

## 📜 Licença

Este projeto é disponibilizado sob uma **Licença MIT**.

É permitido estudar, modificar e compartilhar o projeto gratuitamente.

Consulte [`LICENSE`](LICENSE) para os termos completos.

---

<p align="center">
  <strong>Um novo formato de imagem, construído do zero.</strong>
</p>
