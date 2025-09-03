# ccshortener — URL Shortening Service

Encurtador de URLs em **Java 17+** com **Spring Boot 3**. Fornece API REST para encurtar links, redirecionar por código, definir expiração e contabilizar cliques.

> **Stack:** Spring Boot · Spring Web · Spring Data JPA · PostgreSQL · Flyway · Lombok · Springdoc OpenAPI · JUnit/Jupiter · RestAssured · JaCoCo · Checkstyle · OWASP Dependency‑Check · SonarQube.

---

## Sumário
- [Visão Geral](#visão-geral)
- [Arquitetura & Domínio](#arquitetura--domínio)
- [Requisitos](#requisitos)
- [Como rodar](#como-rodar)
    - [Com Docker (PostgreSQL)](#com-docker-postgresql)
    - [Local com Maven](#local-com-maven)
- [Configurações](#configurações)
- [API](#api)
    - [POST /api/shorten](#post-apishorten)
    - [GET /{code} (redirect)](#get-code-redirect)
    - [Admin opcional](#admin-opcional)
- [Qualidade de Código](#qualidade-de-código)
- [CI/CD (GitHub Actions + SonarQube)](#cicd-github-actions--sonarqube)
- [Roadmap](#roadmap)
- [Licença](#licença)

---

## Visão Geral
O serviço gera um **código curto** (ex.: `4TQp6hv`) para uma URL de destino e oferece:
- Redirecionamento 302 pelo código
- Expiração opcional por data/hora
- Contador de cliques
- (Opcional) registro do IP de criação

---

## Arquitetura & Domínio
Entidade principal `url_mapping` (esquema `ccshortener`), campos típicos:

| Campo        | Tipo         | Observações                                  |
|--------------|--------------|----------------------------------------------|
| `id`         | BIGINT (PK)  | Identificador                                |
| `code`       | VARCHAR(25)  | **Único**. Código curto do link              |
| `target_url` | TEXT         | URL de destino                               |
| `created_at` | TIMESTAMP    | Data/hora de criação                         |
| `expires_at` | TIMESTAMP    | Opcional. Expiração do short link            |
| `active`     | BOOLEAN      | Ativo/Inativo                                |
| `clicks`     | BIGINT       | Total de cliques                             |
| `created_ip` | VARCHAR(45)  | (Opc.) IPv4/IPv6 de quem criou               |

Regras:
- Redireciona apenas quando `active = true` e (se houver) `expires_at` ainda não expirou.
- `code` é único e gerado no backend.

---

## Requisitos
- **JDK 17+**
- **Maven 3.9+**
- **PostgreSQL 15+** (local ou container)
- Docker (opcional, para subir o banco rapidamente)

---

## Como rodar

### Com Docker (PostgreSQL)
Crie um `docker-compose.yml` simples para o banco:

```yaml
services:
  postgres:
    image: postgres:15
    container_name: pg-ccshortener
    environment:
      POSTGRES_DB: ccshortener
      POSTGRES_USER: ccshortener
      POSTGRES_PASSWORD: ccshortener
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ccshortener -d ccshortener"]
      interval: 10s
      timeout: 5s
      retries: 5
```

Suba o banco:
```bash
docker compose up -d
```

### Local com Maven
Configure as variáveis (ou `application-*.yml`):

```bash
# Variáveis de ambiente (Linux/Mac):
export DATABASE_PGSQL_DBNAME=<nome da base a ser utilizada>
export DATABASE_PGSQL_USERNAME=<user name para a aplicacao conectar a base>
export DATABASE_PGSQL_PASSWORD=<senha de conecão com a base>
export DATABASE_PGSQL_URL=<url de conexão. ex: jdbc:postgresql://localhost:5432/ccshortener>
export DATABASE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
export FLYWAY_USERNAME=<usuário para o flyway conectar a base>
export FLYWAY_PASSWORD=<senha para o flyway conectar a base>
export FLYWAY_SCHEMAS=<schema a ser utilizado - ex: ccshortener>
export HOST_URL=<o endereço que vai responder a api - ex: http://seuseite.com/api>
# opcional:
export SPRING_PROFILES_ACTIVE=dev
```

Build + testes + run:
```bash
mvn clean verify
mvn spring-boot:run
# ou
java -jar target/ccshortener-*.jar
```

---

## Configurações
Principais propriedades (perfil `dev`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ccshortener
spring.datasource.username=ccshortener
spring.datasource.password=ccshortener

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=false

spring.flyway.enabled=true
spring.flyway.schemas=ccshortener
```

> Ajuste conforme seu ambiente. Se usar H2 para testes, mantenha `schema` e `migrations` compatíveis.

---

## API

### POST `/api/shorten`
Cria um código curto para a URL informada (com expiração opcional).

**Request**
```json
{
  "targetUrl": "https://google.com"
}
```

**Response 201**
```json
{
  "code": "4TQp6hv",
  "shortUrl": "http://localhost:8080/4TQp6hv",
  "targetUrl": "https://google.com",
  "expiresAt": "2025-12-31T23:59:59",
  "active": true
}
```

Erros comuns:
- `400` URL inválida

### GET `/{code}` (redirect)
Redireciona para a `targetUrl` se ativo e não expirado.

**Response**
- `302 Found` + `Location: https://google.com`
- `404` se código não existe/inativo/expirado

## Qualidade de Código

### Checkstyle
Regras em `config/checkstyle/`. Supressões para permitir JUnit/Hamcrest **somente** em `src/test/java`:
```xml
<!DOCTYPE suppressions PUBLIC
  "-//Checkstyle//DTD SuppressionFilter Configuration 1.2//EN"
  "https://checkstyle.org/dtds/suppressions_1_2.dtd">
<suppressions>
  <suppress files=".*[\\/]src[\\/]test[\\/]java[\\/].*" checks="IllegalImport"
            message=".*(junit|org\.junit|org\.junit\.jupiter|org\.hamcrest).*"/>
</suppressions>
```

Executar:
```bash
mvn -DskipTests=false verify
```

### Cobertura (JaCoCo)
Relatório em `target/site/jacoco/index.html`. A configuração típica no `pom`:

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution>
      <goals><goal>prepare-agent</goal></goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>verify</phase>
      <goals><goal>report</goal></goals>
    </execution>
  </executions>
</plugin>
```
---

## Licença
Defina a licença (ex.: MIT) no arquivo `LICENSE`.

---

### Autor
Cesar Castro — contribuições são bem-vindas via PRs e Issues.
