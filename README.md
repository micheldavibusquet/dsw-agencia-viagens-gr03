# API Agência de Viagens — GR03 SENAI

API RESTful desenvolvida com Java 21, Spring Boot 3.2.5, Spring Data JPA
e Spring Security com autenticação JWT.

## Tecnologias

- Java 21
- Spring Boot 3.2.5
- Spring Data JPA + Hibernate
- Spring Security + JWT
- PostgreSQL 17
- Maven 3.9.16

## Como executar

1. Instale o PostgreSQL e crie o banco:

```sql
   CREATE DATABASE viagens_db;
```

2. Configure as credenciais em `src/main/resources/application.properties`

3. Execute:

```bash
   mvn spring-boot:run
```

4. A API estará disponível em `http://localhost:8080`

## Autenticação

### Cadastrar usuário

**POST** /auth/cadastro

```json
{
  "username": "michel",
  "senha": "123456"
}
```

### Login

**POST** /auth/login

```json
{
  "username": "michel",
  "senha": "123456"
}
```

Retorna o token JWT — use em todas as requisições:
`Authorization: Bearer {token}`

## Perfis de Acesso

| Role       | Permissões                                                 |
| ---------- | ---------------------------------------------------------- |
| ROLE_USER  | Listar, pesquisar, visualizar e reservar destinos          |
| ROLE_ADMIN | Todas as operações + cadastrar, avaliar e excluir destinos |

## Endpoints

### Destinos

| Método | URL                        | Permissão   |
| ------ | -------------------------- | ----------- |
| POST   | /destinos                  | ROLE_ADMIN  |
| GET    | /destinos                  | Autenticado |
| GET    | /destinos/pesquisar?termo= | Autenticado |
| GET    | /destinos/{id}             | Autenticado |
| PATCH  | /destinos/{id}/avaliar     | Autenticado |
| DELETE | /destinos/{id}             | ROLE_ADMIN  |

### Reservas

| Método | URL                     | Permissão   |
| ------ | ----------------------- | ----------- |
| POST   | /destinos/{id}/reservar | Autenticado |
| GET    | /destinos/{id}/reservas | Autenticado |

## Exemplo de teste — Rio de Janeiro

### 1. Cadastrar destino (ROLE_ADMIN)

**POST** /destinos

```json
{
  "nome": "Rio de Janeiro",
  "localizacao": "Rio de Janeiro",
  "descricao": "Cidade Maravilhosa, lar do Cristo Redentor e do Carnaval",
  "preco": 2500.0
}
```

### 2. Reservar destino (Autenticado)

**POST** /destinos/1/reservar

```json
{
  "nomeCliente": "Michel Busquet",
  "emailCliente": "michel@email.com",
  "dataViagem": "2025-12-20",
  "quantidadePessoas": 2
}
```

Retorna valorTotal calculado automaticamente: R$ 5.000,00

## Estrutura do Projeto

```
src/main/java/com/agencia/viagens/
├── config/        → SecurityConfig (Spring Security)
├── controller/    → AuthController, DestinoController, ReservaController
├── dto/           → DTOs de entrada (Login, Destino, Avaliacao, Reserva)
├── exception/     → DestinoNotFoundException, GlobalExceptionHandler
├── model/         → Entidades JPA (Destino, Usuario) e modelo Reserva
├── repository/    → DestinoRepository, UsuarioRepository (Spring Data JPA)
├── security/      → JwtUtil, JwtAuthFilter
└── service/       → DestinoService, ReservaService, UsuarioDetailsService
```
