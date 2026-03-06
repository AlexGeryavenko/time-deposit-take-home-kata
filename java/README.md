# Time Deposit API

REST API for managing time deposits with automated monthly interest calculation. Built with Spring Boot 3.2, Hexagonal
Architecture, and PostgreSQL.

## Prerequisites

| Dependency | Version | Install                                                                                                                                                                            |
|------------|---------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Java JDK   | 17+     | macOS: `brew install openjdk@17` / Linux: `sudo apt install openjdk-17-jdk` / Windows: [Adoptium](https://adoptium.net/)                                                           |
| Maven      | 3.9+    | macOS: `brew install maven` / Linux: `sudo apt install maven` / Windows: [Maven download](https://maven.apache.org/download.cgi)                                                   |
| Docker     | 24+     | macOS: `brew install --cask docker` / Linux: [Docker Engine](https://docs.docker.com/engine/install/) / Windows: [Docker Desktop](https://www.docker.com/products/docker-desktop/) |

## Quick Start

```bash
cd java
mvn spring-boot:test-run
```

This starts a PostgreSQL 16 container via Testcontainers, runs Liquibase migrations, and serves the API on `http://localhost:8080`.

## Commands

| Command                            | Description                        |
|------------------------------------|------------------------------------|
| `mvn spring-boot:test-run`         | Start app with auto-provisioned DB |
| `mvn spring-boot:test-run -Pdebug` | Start with remote debugger on 5005 |
| `mvn test`                         | Run all tests (unit + integration) |
| `mvn verify`                       | Run tests + JaCoCo coverage report |

## API Usage

All `/api/**` endpoints require an API key via the `X-API-Key` header. The dev key is `changeme-dev-key`.

**Update all balances** — calculates and applies monthly interest:

```bash
curl -X POST http://localhost:8080/api/v1/time-deposits/update-balances \
  -H "X-API-Key: changeme-dev-key"
```

```json
[
  {
    "id": 1,
    "planType": "basic",
    "balance": 1000.83,
    "days": 31,
    "withdrawals": []
  }
]
```

**Get all deposits** — returns deposits with their withdrawals (paginated):

```bash
curl http://localhost:8080/api/v1/time-deposits \
  -H "X-API-Key: changeme-dev-key"
```

```json
{
  "content": [
    {
      "id": 1,
      "planType": "student",
      "balance": 2005.00,
      "days": 100,
      "withdrawals": [
        {
          "id": 1,
          "amount": 200.00,
          "date": "2026-01-15"
        }
      ]
    }
  ],
  "totalElements": 10,
  "totalPages": 1,
  "size": 20,
  "number": 0,
  "first": true,
  "last": true,
  "empty": false
}
```

## Swagger / OpenAPI

The API is documented using an [OpenAPI 3.0 contract](src/main/resources/openapi/api.yaml) and exposed via Swagger UI at runtime.
This provides an interactive interface to explore endpoints, view request/response schemas, and execute API calls directly from
the browser — no curl or Postman needed.

**Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### API Contract

| Endpoint                                | Method | Description                                          |
|-----------------------------------------|--------|------------------------------------------------------|
| `/api/v1/time-deposits/update-balances` | `POST` | Calculate and apply monthly interest to all deposits |
| `/api/v1/time-deposits`                 | `GET`  | List all deposits with withdrawals (paginated)       |

### Key Parameters

**GET /time-deposits:**

| Parameter | In    | Required | Default  | Description                   |
|-----------|-------|----------|----------|-------------------------------|
| `page`    | query | No       | `0`      | Zero-based page index         |
| `size`    | query | No       | `20`     | Page size (1–100)             |
| `sort`    | query | No       | `id,asc` | Sort criteria (e.g. `id,asc`) |

### Swagger UI Usage Example

1. Start the app: `mvn spring-boot:test-run`
2. Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) in your browser
3. Click **Authorize** and enter `changeme-dev-key`
4. Expand an endpoint, click **Try it out**, fill in parameters, and click **Execute**

Example — get second page of deposits sorted by balance:

```
GET /api/v1/time-deposits?page=1&size=10&sort=balance,desc
```

### Response Schemas

**TimeDepositResponse:**

```json
{
  "id": 1,
  "planType": "basic",
  "balance": 1000.83,
  "days": 31,
  "withdrawals": []
}
```

**ErrorResponse** (returned for 400, 401, 429, 500):

```json
{
  "error": "TOO_MANY_REQUESTS",
  "message": "Rate limit exceeded.",
  "status": 429,
  "timestamp": "2026-03-05T10:00:00.000Z"
}
```

## Documentation

- [System Design](doc/system-design.md) — architecture, database schema, security, and design decisions
- [Manual Testing](doc/manual-testing.md) — curl commands, seed data, and expected results
