[🇺🇸 English](README.en.md)
# DMInfo

> Sistema de gerenciamento de instituição desenvolvido ao longo de três semestres da disciplina de Engenharia de Software (BSI — UNOESTE), cobrindo desde o levantamento de requisitos até a implementação com padrões de projeto.

---

## Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura](#arquitetura)
- [Engenharia de Software Aplicada](#engenharia-de-software-aplicada)
  - [ES1 — Requisitos e Modelagem](#es1--requisitos-e-modelagem)
  - [ES2 — Implementação e Qualidade](#es2--implementação-e-qualidade)
  - [ES3 — Padrões de Projeto e Manutenção](#es3--padrões-de-projeto-e-manutenção)
- [Estrutura do Repositório](#estrutura-do-repositório)
- [Pré-requisitos](#pré-requisitos)
- [Endpoints](#endpoints)
- [Banco de Dados](#banco-de-dados)

---

## Sobre o Projeto

O **DMInfo** é uma API REST para gerenciamento de uma instituição, desenvolvida com Java Spring Boot e PostgreSQL, seguindo o padrão arquitetural MVC. O projeto acompanha o ciclo completo de desenvolvimento de software: da análise e especificação de requisitos à codificação, testes e aplicação de padrões de projeto.

O desenvolvimento ocorreu em três fases correspondentes às disciplinas de Engenharia de Software I, II e III do curso de Sistemas de Informação.

---

## Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17+ |
| Framework | Spring Boot |
| Banco de dados | PostgreSQL |
| Frontend | HTML, CSS, JavaScript |
| Controle de versão | Git / GitHub |

---

## Arquitetura

O sistema segue o padrão **MVC (Model-View-Controller)**:

```
src/
└── main/
    ├── java/
    │   └── com/dminfo/
    │       ├── controller/   ← Recebe requisições HTTP, delega à camada de serviço
    │       ├── service/      ← Regras de negócio e orquestração
    │       ├── model/        ← Entidades e mapeamento objeto-relacional
    │       └── repository/   ← Acesso ao banco de dados (Spring Data JPA)
    └── resources/
        ├── static/           ← Arquivos de frontend (HTML, CSS, JS)
        └── application.properties
```

O mapeamento objeto-relacional foi implementado via **JPA/Hibernate**, associando as entidades Java às tabelas do PostgreSQL.

---

## Engenharia de Software Aplicada

Esta seção documenta as práticas, artefatos e decisões de engenharia aplicadas em cada semestre da disciplina.

### ES1 — Requisitos e Modelagem

**Disciplina:** Engenharia de Software I  
**Foco:** Levantamento de requisitos, análise orientada a objetos e especificação do sistema.

#### Processo de desenvolvimento adotado

O projeto seguiu um **processo iterativo e incremental**, com elementos do Unified Process (UP). As iterações foram organizadas em ciclos curtos com entregas parciais validadas pelo professor, aproximando-se do framework **Scrum** na definição de papéis (Product Owner, Scrum Master, Dev Team) e no uso de backlog de produto.

#### Engenharia de Requisitos

Os requisitos foram obtidos por meio de entrevistas e análise de domínio, classificados em:

- **Requisitos Funcionais (RF):** descrevem o que o sistema deve fazer — cadastros, consultas, operações de negócio.
- **Requisitos Não Funcionais (RNF):** desempenho, usabilidade, segurança e portabilidade.
- **Requisitos de Domínio:** regras específicas da instituição gerenciada.

#### Artefatos produzidos (ES1)

| Artefato | Descrição |
|---|---|
| ERS (Especificação de Requisitos de Software) | Documento completo com escopo, requisitos funcionais e não funcionais |
| Lista de Funções do Produto | Inventário de funcionalidades derivadas dos requisitos |
| Diagrama de Casos de Uso | Atores, casos de uso e relacionamentos (include/extend) |
| Especificações de Casos de Uso | Fluxos principal, alternativo e de exceção por caso de uso |
| Diagramas de Atividades | Fluxos de controle para os casos de uso principais |
| Modelo Conceitual | Entidades do domínio e seus relacionamentos |
| Diagrama de Sequência | Interações entre objetos nos cenários principais |
| Diagrama de Classes | Classes, atributos, métodos e associações |

> A documentação completa (ERS, diagramas UML) encontra-se em repositório/drive externo da equipe, referenciado pelo professor da disciplina.

---

### ES2 — Implementação e Qualidade

**Disciplina:** Engenharia de Software II  
**Foco:** Implementação do projeto, mapeamento objeto-relacional, controle de versão e princípios SOLID.

#### Revisão da modelagem

No início deste semestre foi realizada uma **revisão técnica formal** dos artefatos produzidos na ES1, identificando inconsistências nos diagramas de classes e ajustando o modelo conceitual antes do início da codificação.

#### Mapeamento Objeto-Relacional

As entidades Java foram mapeadas para o banco de dados PostgreSQL utilizando **JPA/Hibernate**. As decisões de mapeamento seguiram o modelo conceitual aprovado na ES1.

#### Gestão da Configuração

O controle de versão foi gerenciado com **Git/GitHub**, aplicando as práticas:

- Controle de versão com histórico de commits descritivos
- Controle de modificação via Pull Requests (quando aplicável)
- Branches por funcionalidade

#### Princípios SOLID aplicados

| Princípio | Aplicação no projeto |
|---|---|
| **S** — Single Responsibility | Cada classe possui responsabilidade única (Controller, Service, Repository separados) |
| **O** — Open/Closed | Uso de interfaces e abstrações para extensão sem modificação |
| **L** — Liskov Substitution | Hierarquias de herança respeitam contratos de supertipo |
| **I** — Interface Segregation | Interfaces específicas por contexto de uso |
| **D** — Dependency Inversion | Injeção de dependências gerenciada pelo Spring IoC Container |

---

### ES3 — Padrões de Projeto e Manutenção

**Disciplina:** Engenharia de Software III  
**Foco:** Aplicação de padrões GoF, GRASP, qualidade de software e manutenibilidade.

#### Processos ágeis aplicados

O desenvolvimento neste semestre utilizou **Kanban** para gestão do fluxo de trabalho, com colunas de Backlog, Em Progresso, Em Revisão e Concluído. O Kanban complementou o ritmo iterativo já estabelecido, dando maior visibilidade ao estado das tarefas.

#### Padrões GoF (Gang of Four) aplicados

**Padrões Criacionais**

| Padrão | Contexto de aplicação |
|---|---|
| — | *(a preencher conforme implementação)* |

**Padrões Estruturais**

| Padrão | Contexto de aplicação |
|---|---|
| — | *(a preencher conforme implementação)* |

**Padrões Comportamentais**

| Padrão | Contexto de aplicação |
|---|---|
| — | *(a preencher conforme implementação)* |

> Esta seção será atualizada conforme os padrões forem identificados e implementados ao longo da ES3.

#### GRASP (General Responsibility Assignment Software Patterns)

Os padrões GRASP orientaram a distribuição de responsabilidades entre as classes do sistema, reforçando decisões já tomadas na modelagem da ES1.

#### Qualidade de Software

**Verificação e Validação:**

- **Revisões técnicas formais** aplicadas sobre código e documentação
- **Testes funcionais** cobrindo os fluxos descritos nas especificações de casos de uso
- **Testes estruturais** sobre as unidades de negócio

**Manutenção e Reengenharia:**

Ao longo do desenvolvimento foram identificadas oportunidades de refatoração derivadas da aplicação dos padrões GoF e dos princípios SOLID, caracterizando ciclos de **manutenção evolutiva** do sistema.

---

## Estrutura do Repositório

```
Eng2-BSI-2025-S2-Grupo1/
├── Arquivos/          ← Artefatos auxiliares (scripts, exemplos, etc.)
├── DMInfo/            ← Código-fonte da aplicação Spring Boot
│   └── src/
├── .gitignore
└── README.md
```

---

## Pré-requisitos

- Java 17 ou superior
- Maven
- PostgreSQL 14 ou superior
- Git

---

## Endpoints

> Documentação detalhada dos endpoints a ser adicionada conforme a API for finalizada. Abaixo a estrutura base:

| Método | Rota | Descrição |
|---|---|---|
| GET | `/` | Frontend da aplicação |
| GET | `/api/...` | *(a preencher)* |
| POST | `/api/...` | *(a preencher)* |
| PUT | `/api/...` | *(a preencher)* |
| DELETE | `/api/...` | *(a preencher)* |

---

## Banco de Dados

O modelo relacional foi derivado do **Modelo Conceitual** produzido na ES1, passando pelo processo de **mapeamento objeto-relacional** com JPA/Hibernate.

O script de criação do banco (DDL) está disponível em `Arquivos/` *(se aplicável)*.

---

*Projeto acadêmico — Bacharelado em Sistemas de Informação — UNOESTE*
