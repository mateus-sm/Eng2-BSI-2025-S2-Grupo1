[🇧🇷 Português](README.md)

# DMInfo

> Institution management system developed over three semesters of the Software Engineering course (BSI — UNOESTE), covering everything from requirements gathering to implementation with design patterns.

---

## Table of Contents

- [About the Project](#about-the-project)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Applied Software Engineering](#applied-software-engineering)
  - [SE1 — Requirements and Modeling](#se1--requirements-and-modeling)
  - [SE2 — Implementation and Quality](#se2--implementation-and-quality)
  - [SE3 — Design Patterns and Maintenance](#se3--design-patterns-and-maintenance)
- [Repository Structure](#repository-structure)
- [Prerequisites](#prerequisites)
- [Endpoints](#endpoints)
- [Database](#database)

---

## About the Project

**DMInfo** is a REST API for institution management, developed with Java Spring Boot and PostgreSQL, following the MVC architectural pattern. The project covers the complete software development lifecycle: from requirements analysis and specification to coding, testing, and design pattern application.

Development took place in three phases corresponding to the Software Engineering I, II, and III courses in the Information Systems degree program.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot |
| Database | PostgreSQL |
| Frontend | HTML, CSS, JavaScript |
| Version Control | Git / GitHub |

---

## Architecture

The system follows the **MVC (Model-View-Controller)** pattern:

```
src/
└── main/
    ├── java/
    │   └── com/dminfo/
    │       ├── controller/   ← Receives HTTP requests, delegates to the service layer
    │       ├── service/      ← Business rules and orchestration
    │       ├── model/        ← Entities and object-relational mapping
    │       └── repository/   ← Database access (Spring Data JPA)
    └── resources/
        ├── static/           ← Frontend files (HTML, CSS, JS)
        └── application.properties
```

Object-relational mapping was implemented via **JPA/Hibernate**, associating Java entities with PostgreSQL tables.

---

## Applied Software Engineering

This section documents the practices, artifacts, and engineering decisions applied in each semester of the course.

### SE1 — Requirements and Modeling

**Course:** Software Engineering I  
**Focus:** Requirements gathering, object-oriented analysis, and system specification.

#### Adopted development process

The project followed an **iterative and incremental process**, with elements from the Unified Process (UP). Iterations were organized into short cycles with partial deliverables validated by the instructor, closely resembling the **Scrum** framework in its role definitions (Product Owner, Scrum Master, Dev Team) and the use of a product backlog.

#### Requirements Engineering

Requirements were gathered through interviews and domain analysis, classified as:

- **Functional Requirements (FR):** describe what the system must do — registrations, queries, business operations.
- **Non-Functional Requirements (NFR):** performance, usability, security, and portability.
- **Domain Requirements:** rules specific to the managed institution.

#### Artifacts produced (SE1)

| Artifact | Description |
|---|---|
| SRS (Software Requirements Specification) | Complete document with scope, functional and non-functional requirements |
| Product Function List | Inventory of features derived from the requirements |
| Use Case Diagram | Actors, use cases, and relationships (include/extend) |
| Use Case Specifications | Main, alternative, and exception flows per use case |
| Activity Diagrams | Control flows for the main use cases |
| Conceptual Model | Domain entities and their relationships |
| Sequence Diagram | Object interactions in the main scenarios |
| Class Diagram | Classes, attributes, methods, and associations |

> The complete documentation (SRS, UML diagrams) is stored in an external repository/drive managed by the team, as referenced by the course instructor.

---

### SE2 — Implementation and Quality

**Course:** Software Engineering II  
**Focus:** Project implementation, object-relational mapping, version control, and SOLID principles.

#### Modeling review

At the beginning of this semester, a **formal technical review** of the artifacts produced in SE1 was conducted, identifying inconsistencies in the class diagrams and adjusting the conceptual model prior to the start of coding.

#### Object-Relational Mapping

Java entities were mapped to the PostgreSQL database using **JPA/Hibernate**. Mapping decisions followed the conceptual model approved in SE1.

#### Configuration Management

Version control was managed with **Git/GitHub**, applying the following practices:

- Version control with descriptive commit history
- Change control via Pull Requests (where applicable)
- Feature branches

#### SOLID principles applied

| Principle | Application in the project |
|---|---|
| **S** — Single Responsibility | Each class has a single responsibility (Controller, Service, Repository separated) |
| **O** — Open/Closed | Use of interfaces and abstractions for extension without modification |
| **L** — Liskov Substitution | Inheritance hierarchies respect supertype contracts |
| **I** — Interface Segregation | Specific interfaces per usage context |
| **D** — Dependency Inversion | Dependency injection managed by the Spring IoC Container |

---

### SE3 — Design Patterns and Maintenance

**Course:** Software Engineering III  
**Focus:** Application of GoF and GRASP patterns, software quality, and maintainability.

#### Agile processes applied

Development this semester used **Kanban** for workflow management, with Backlog, In Progress, In Review, and Done columns. Kanban complemented the iterative pace already established, providing greater visibility into the state of tasks.

#### GoF (Gang of Four) patterns applied

**Creational Patterns**

| Pattern | Application context |
|---|---|
| — | *(to be filled in as implemented)* |

**Structural Patterns**

| Pattern | Application context |
|---|---|
| — | *(to be filled in as implemented)* |

**Behavioral Patterns**

| Pattern | Application context |
|---|---|
| — | *(to be filled in as implemented)* |

> This section will be updated as patterns are identified and implemented throughout SE3.

#### GRASP (General Responsibility Assignment Software Patterns)

GRASP patterns guided the distribution of responsibilities among the system's classes, reinforcing decisions already made during SE1 modeling.

#### Software Quality

**Verification and Validation:**

- **Formal technical reviews** applied to code and documentation
- **Functional tests** covering the flows described in the use case specifications
- **Structural tests** on business units

**Maintenance and Reengineering:**

Throughout development, refactoring opportunities were identified stemming from the application of GoF patterns and SOLID principles, characterizing cycles of **evolutionary maintenance** of the system.

---

## Repository Structure

```
Eng2-BSI-2025-S2-Grupo1/
├── Arquivos/          ← Auxiliary artifacts (scripts, examples, etc.)
├── DMInfo/            ← Spring Boot application source code
│   └── src/
├── .gitignore
└── README.md
```

---

## Prerequisites

- Java 17 or higher
- Maven
- PostgreSQL 14 or higher
- Git

---

## Endpoints

> Detailed endpoint documentation to be added as the API is finalized. Base structure below:

| Method | Route | Description |
|---|---|---|
| GET | `/` | Application frontend |
| GET | `/api/...` | *(to be filled in)* |
| POST | `/api/...` | *(to be filled in)* |
| PUT | `/api/...` | *(to be filled in)* |
| DELETE | `/api/...` | *(to be filled in)* |

---

## Database

The relational model was derived from the **Conceptual Model** produced in SE1, going through the **object-relational mapping** process with JPA/Hibernate.

The database creation script (DDL) is available in `Arquivos/` *(if applicable)*.

---

*Academic project — Bachelor's in Information Systems — UNOESTE*
