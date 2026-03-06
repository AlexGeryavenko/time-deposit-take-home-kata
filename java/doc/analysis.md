# Time Deposit Kata — Java Codebase Analysis

## 1. Task Requirements

### REST API Endpoints

1. **POST** — Update all balances (calls `updateBalance` on all deposits)
2. **GET** — Retrieve all deposits (including associated withdrawals)

### Database Schema

- **timeDeposits**: `id` (PK), `planType`, `days`, `balance`
- **withdrawals**: `id` (PK), `timeDepositId` (FK), `amount`, `date`

### Architecture Preferences

- OpenAPI / Swagger documentation
- Hexagonal Architecture (ports & adapters)
- Testcontainers for integration tests
- Atomic commits showing TDD red-green-refactor progression

### Constraints

- **Do not change** the `TimeDeposit` class interface or the `updateBalance(List<TimeDeposit>)` method signature
- The existing `updateBalance` logic is considered correct — refactoring must preserve identical behavior

---

## 2. Evaluation Criteria

| Area                    | What They're Looking For                                                                                     |
|-------------------------|--------------------------------------------------------------------------------------------------------------|
| **Parameterized tests** | Replace placeholder `1 == 1` with proper parameterized tests covering all plan types and boundary conditions |
| **Refactoring**         | Improve nested if/else in `updateBalance` while preserving behavior                                          |
| **SOLID principles**    | SRP + OCP — e.g., strategy pattern per plan type so new plans don't require modifying existing code          |
| **TDD**                 | Red-green-refactor cycle visible in commit history                                                           |

---

## 3. Current Code Review

### `TimeDeposit.java` (model class)

- Simple POJO with constructor + getters + one setter (`setBalance`)
- Uses `Double` wrapper type (not primitive `double`) — allows null but introduces boxing overhead
- No `equals()`, `hashCode()`, or `toString()` — makes test assertions harder
- Only `setBalance` is mutable — `days` and `planType` are effectively immutable after construction
- `planType` is a raw `String` — no type safety, prone to typos

### `TimeDepositCalculator.java` (core logic)

- Single method `updateBalance(List<TimeDeposit> xs)` — mutates deposits in-place
- Deeply nested if/else structure (3 levels deep)
- Parameter named `xs` — non-descriptive
- Uses index-based loop (`for (int i = 0; ...)`) instead of enhanced for-each
- Repeated `xs.get(i)` calls (6 times) — should extract to local variable
- Variable `a2d` — cryptic name, presumably "amount to deposit" or similar
- Unnecessary BigDecimal conversion pattern: creates `BigDecimal` from `double` just to round, then converts back to `double`
- Magic numbers throughout (see section 4)

### `TimeDepositCalculatorTest.java` (test file)

- Single test method with placeholder assertion `assertThat(1).isEqualTo(1)`
- Creates a deposit and calls `updateBalance` but never asserts on the result
- No parameterized tests, no edge cases, no boundary testing
- Missing `junit-jupiter-params` dependency in `pom.xml` for `@ParameterizedTest`

---

## 4. Code Issues & Smells

### Magic Numbers

| Value  | Meaning                                                                       |
|--------|-------------------------------------------------------------------------------|
| `0.01` | Basic annual rate (1%)                                                        |
| `0.03` | Student annual rate (3%)                                                      |
| `0.05` | Premium annual rate (5%)                                                      |
| `12`   | Months per year (monthly interest divisor)                                    |
| `30`   | Minimum days before any interest accrues                                      |
| `45`   | Premium plan: interest starts after this day                                  |
| `366`  | Student plan: no interest on or after this day (i.e., interest up to day 365) |

### String-Based Plan Types

Plan types are compared with `.equals("student")`, `.equals("premium")`, `.equals("basic")` — should be an enum.

### Index-Based Loop

```java
for(int i = 0; i <xs.

size();

i++){xs.

get(i)...}
```

Should be `for (TimeDeposit deposit : deposits)`.

### BigDecimal Conversion Pattern

```java
double a2d = xs.get(i).getBalance() + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
```

- Creates a `BigDecimal` from a `double` (which itself can have precision issues)
- Only rounds the **interest** portion, not the final balance
- The rounded interest is added back to a `double` balance — precision is not consistently maintained

---

## 5. Boundary Conditions Table

Based on the actual conditional logic in `TimeDepositCalculator`:

```
days > 30           → any interest possible
days < 366          → student interest applies
days > 45           → premium interest applies
```

| Days | Basic | Student | Premium | Rationale                                           |
|------|-------|---------|---------|-----------------------------------------------------|
| 0    | No    | No      | No      | `days > 30` is false                                |
| 30   | No    | No      | No      | `30 > 30` is false                                  |
| 31   | Yes   | Yes     | No      | `31 > 30` true; `31 < 366` true; `31 > 45` false    |
| 45   | Yes   | Yes     | No      | `45 > 30` true; `45 < 366` true; `45 > 45` false    |
| 46   | Yes   | Yes     | Yes     | `46 > 30` true; `46 < 366` true; `46 > 45` true     |
| 365  | Yes   | Yes     | Yes     | `365 > 30` true; `365 < 366` true; `365 > 45` true  |
| 366  | Yes   | No      | Yes     | `366 > 30` true; `366 < 366` false; `366 > 45` true |

---

## 6. Interest Calculation Examples

### Formula

```
interest = balance * rate / 12   (rounded to 2 decimal places, HALF_UP)
new_balance = balance + rounded_interest
```

### Rates

- Basic: 0.01 (1%)
- Student: 0.03 (3%)
- Premium: 0.05 (5%)

### With balance = 1,234,567.00

| Plan    | Days | Interest | New Balance  | Notes                                            |
|---------|------|----------|--------------|--------------------------------------------------|
| Basic   | 45   | 1,028.81 | 1,235,595.81 | `1234567 * 0.01 / 12 = 1028.80583...` → 1,028.81 |
| Student | 45   | 3,086.42 | 1,237,653.42 | `1234567 * 0.03 / 12 = 3086.4175` → 3,086.42     |
| Premium | 46   | 5,144.03 | 1,239,711.03 | `1234567 * 0.05 / 12 = 5144.02916...` → 5,144.03 |

### With balance = 1,000.00

| Plan    | Days | Interest | New Balance |
|---------|------|----------|-------------|
| Basic   | 31   | 0.83     | 1,000.83    |
| Student | 31   | 2.50     | 1,002.50    |
| Premium | 46   | 4.17     | 1,004.17    |

### Zero-interest cases (balance = 1,000.00)

| Plan    | Days | Interest | New Balance |
|---------|------|----------|-------------|
| Any     | 0    | 0.00     | 1,000.00    |
| Any     | 30   | 0.00     | 1,000.00    |
| Premium | 45   | 0.00     | 1,000.00    |
| Student | 366  | 0.00     | 1,000.00    |

---

## 7. Constraints Summary

1. **Cannot change** `TimeDeposit` class interface (fields, constructor, getters, setter)
2. **Cannot change** `updateBalance(List<TimeDeposit>)` method signature
3. Existing calculation logic is the **source of truth** — refactored code must produce identical results
4. Hexagonal architecture preferred — separate domain logic from infrastructure (DB, REST)
5. Need `junit-jupiter-params` dependency added to `pom.xml` for parameterized tests
6. Java 17 is the target version (set in `pom.xml`)
