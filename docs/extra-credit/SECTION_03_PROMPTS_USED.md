# Section 3: Prompts Used (Cursor IDE — AWRS Extra Credit)

**Project:** Automated Warehouse Restock System (AWRS)  
**Tool:** Cursor IDE (AI-assisted development)  
**Stack:** Java 17, Maven, JUnit 5, Mockito  

All prompts below were used with `@` file references in Cursor to provide SRS and story context.

---

## Demo 1 — Domain Models & Authentication

### Prompt 1: Story Analysis (STORY-01)

```
Read @docs/stories/STORY-01-user-authentication.md and the AWRS SRS.
Do not write code yet.

Return:
1. story summary
2. acceptance criteria (numbered)
3. backend tasks for Demo 1
4. frontend tasks (JavaFX, later iteration)
5. which test classes this story maps to
```

### Prompt 2: Minimal Design (Demo 1)

```
Using @docs/stories/STORY-01-user-authentication.md and @docs/stories/STORY-02-item-catalog-locations.md,
generate minimal Mermaid content for:
1. use case diagram (login, role check)
2. sequence diagram (successful login + failed login)
3. class diagram (User, Item, WarehouseLocation, AuthService, repositories)

Keep diagrams relevant to Demo 1 only.
Do not generate Java code.
Save output to @docs/design/
```

### Prompt 3: Implementation Plan (Demo data-driven)

```
Create a minimal implementation plan for Demo 1 (User, Item, WarehouseLocation, AuthService).

Constraints:
- strict TDD — tests before production code
- model classes first, then AuthService
- in-memory repositories (no SQLite yet)
- no JavaFX GUI in this demo
- finishable in one class session

List file creation order and test order.
```

### Prompt 4: Domain Model Tests First

```
Write the smallest failing JUnit 5 tests for:
- @src/test/java/.../UserTest.java
- @src/test/java/.../ItemTest.java
- @src/test/java/.../WarehouseLocationTest.java

Cover only:
User: valid creation, empty username rejected, role assignment
Item: valid SKU, duplicate SKU rejected, missing description rejected
WarehouseLocation: hierarchy path, parent-child link, invalid empty name

Use @BeforeEach for setup. Do not implement production model classes yet.
Match @pom.xml JUnit 5 dependencies.
```

### Prompt 5: Domain Model Implementation

```
Implement only enough production code in User.java, Item.java, and WarehouseLocation.java
to make the current failing model tests pass.

Constraints:
- minimal fields per SRS
- no persistence layer yet
- no Lombok unless already in pom.xml
- follow Java naming conventions used in existing files
```

### Prompt 6: AuthService Tests First

```
Write the smallest failing tests for @src/test/java/.../AuthServiceTest.java

Cover only:
- valid login returns user with correct role
- invalid password throws AuthenticationException
- unknown username throws AuthenticationException
- empty username or password throws IllegalArgumentException
- non-Admin cannot create users (role check)

Mock UserRepository with Mockito. Do not implement AuthService yet.
```

### Prompt 7: AuthService Implementation

```
Implement only enough AuthService.java production code to make AuthServiceTest pass.

Constraints:
- inject UserRepository via constructor
- keep login logic minimal — no JWT, no session tokens
- use Optional or exceptions consistently with existing test expectations
```

---

## Demo 2 — Inventory Service

### Prompt 8: Story Analysis (STORY-03)

```
Read @docs/stories/STORY-03-inventory-tracking.md
Do not write code yet.

Return:
1. story summary
2. acceptance criteria
3. backend tasks for InventoryRecord, AuditLog, InventoryService
4. how receive vs fulfill vs adjust differ in audit logging
```

### Prompt 9: InventoryService Tests First

```
Write failing JUnit 5 tests for @src/test/java/.../InventoryServiceTest.java

Cover only:
- receiveShipment increases quantity at location
- receiveShipment creates audit log entry
- fulfillOrder deducts quantity when sufficient stock
- fulfillOrder throws when insufficient stock
- adjustInventory requires Manager role (mock AuthService if needed)

Mock InventoryRepository and AuditLogRepository with Mockito.
Do not implement InventoryService yet.
```

### Prompt 10: InventoryService Implementation

```
Implement InventoryRecord.java, AuditLog.java, InventoryService.java,
InventoryRepository.java, and AuditLogRepository.java
to make InventoryServiceTest pass.

Constraints:
- transactional integrity: if audit log fails, roll back quantity change
- minimal in-memory repository implementations
- reason codes as enum for adjustments
```

---

## Demo 3 — Restock Service

### Prompt 11: Story Analysis (STORY-04)

```
Read @docs/stories/STORY-04-automated-restocking.md
Do not write code yet.

Return:
1. story summary
2. acceptance criteria
3. RestockTask state transitions (Created → Assigned → In Progress → Completed)
4. priority calculation rules in plain English
```

### Prompt 12: RestockService Tests First

```
Write failing tests for @src/test/java/.../RestockServiceTest.java

Cover only:
- evaluateThresholds creates task when quantity below minimum
- no task created when quantity above minimum
- completeTask updates inventory and sets status COMPLETED
- batchEvaluate processes multiple items
- priority higher when closer to stockout

Mock RestockTaskRepository and InventoryService.
Do not implement RestockService yet.
```

### Prompt 13: RestockService Implementation

```
Implement RestockTask.java, RestockService.java, and RestockTaskRepository.java
to make RestockServiceTest pass.

Constraints:
- status enum matches SRS lifecycle
- priority as integer (lower = higher priority)
- no predictive module yet — rule-based thresholds only
```

---

## Integration & Debugging Prompts

### Prompt 14: Maven Verify

```
Run `mvn test` and report any compilation or test failures.
For each failure:
1. likely root cause
2. exact file and line region
3. smallest fix
Do not refactor unrelated code.
```

### Prompt 15: Refactor (after all tests green)

```
All Demo 1–3 tests are green.
Suggest one small safe refactor (e.g., extract duplicate validation in Item and User).
Apply only that refactor and confirm tests still pass.
```

### Prompt 16: Debugging (example failure)

```
RestockServiceTest.testCompleteTaskUpdatesInventory is failing after a change to InventoryService.
Debug systematically:
1. likely root cause
2. exact code region
3. smallest fix
4. no unrelated edits
Show the failing assertion and stack trace analysis.
```

---

## Prompt Refinement Notes

| Iteration | What changed | Why |
|-----------|--------------|-----|
| v1 → v2 | Added `@pom.xml` and explicit JUnit 5 | AI generated JUnit 4 `@Test` imports |
| v2 → v3 | "Do not implement production code yet" on test prompts | AI wrote both tests and impl in one pass |
| v3 → v4 | Listed exact test method names and scenarios | Reduced/verify() usage was inconsistent |
| v4 → v5 | "in-memory repositories only" | AI attempted JDBC/SQLite too early |
