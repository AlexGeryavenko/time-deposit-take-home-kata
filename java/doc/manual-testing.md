# Manual Testing Guide

## Setup

```bash
cd java
mvn spring-boot:test-run
```

- **Swagger UI:** http://localhost:8080/swagger-ui.html (Authorize with `changeme-dev-key`)
- **Stop:** `Ctrl+C`

## Seed Data

| ID | Plan | Days | Balance | Interest? | Why |
|----|------|------|---------|-----------|-----|
| 1 | basic | 29 | 1000.00 | No | Below 30-day threshold |
| 2 | basic | 31 | 1000.00 | Yes | Above 30 days |
| 3 | basic | 365 | 5000.00 | Yes | No cap for basic |
| 4 | student | 30 | 2000.00 | No | At 30-day boundary |
| 5 | student | 31 | 2000.00 | Yes | Above 30 days |
| 6 | student | 365 | 2000.00 | Yes | At 365-day boundary |
| 7 | student | 366 | 2000.00 | No | Past 365-day cap |
| 8 | premium | 44 | 10000.00 | No | Below 45-day threshold |
| 9 | premium | 45 | 10000.00 | No | At 45-day boundary |
| 10 | premium | 46 | 10000.00 | Yes | Above 45 days |

Withdrawals: deposit 3 (500.00), deposit 5 (100.00), deposit 10 (1000.00).

## Test Flow

### 1. Get current state

```bash
curl -s http://localhost:8080/api/v1/time-deposits \
  -H "X-API-Key: changeme-dev-key" | jq .
```

All 10 deposits at original balances. Deposits 3, 5, 10 include withdrawals.

### 2. Update balances

```bash
curl -s -X POST http://localhost:8080/api/v1/time-deposits/update-balances \
  -H "X-API-Key: changeme-dev-key" | jq .
```

| ID | Expected balance |
|----|-----------------|
| 1 | 1000.00 |
| 2 | 1000.83 |
| 3 | 5004.17 |
| 4 | 2000.00 |
| 5 | 2005.00 |
| 6 | 2005.00 |
| 7 | 2000.00 |
| 8 | 10000.00 |
| 9 | 10000.00 |
| 10 | 10041.67 |

### 3. Verify persistence

```bash
curl -s http://localhost:8080/api/v1/time-deposits \
  -H "X-API-Key: changeme-dev-key" | jq '.content[] | {id, balance}'
```

### 4. Second update (compounding)

```bash
curl -s -X POST http://localhost:8080/api/v1/time-deposits/update-balances \
  -H "X-API-Key: changeme-dev-key" | jq '.[] | {id, balance}'
```

| ID | After 1st | After 2nd |
|----|-----------|-----------|
| 2 | 1000.83 | 1001.66 |
| 3 | 5004.17 | 5008.34 |
| 5 | 2005.00 | 2010.01 |
| 6 | 2005.00 | 2010.01 |
| 10 | 10041.67 | 10083.51 |

Non-interest deposits (1, 4, 7, 8, 9) remain unchanged.

### 5. Pagination

```bash
curl -s "http://localhost:8080/api/v1/time-deposits?page=0&size=3" \
  -H "X-API-Key: changeme-dev-key" | jq .

curl -s "http://localhost:8080/api/v1/time-deposits?sort=balance,desc" \
  -H "X-API-Key: changeme-dev-key" | jq .
```

## Error Scenarios

```bash
# Missing API key → 401
curl -s http://localhost:8080/api/v1/time-deposits | jq .

# Invalid API key → 401
curl -s http://localhost:8080/api/v1/time-deposits -H "X-API-Key: wrong-key" | jq .

# Rate limit → 429 (after 60 requests in 60s)
for i in $(seq 1 65); do
  curl -s -o /dev/null -w "%{http_code}\n" \
    http://localhost:8080/api/v1/time-deposits -H "X-API-Key: changeme-dev-key"
done
```
