# Section 5: Errors & Debugging Notes

## 5.1 Compilation Errors

### Error: JUnit 4 vs JUnit 5 imports
**Symptom:** `cannot find symbol: class Before` or `org.junit.Test` not found  
**Cause:** Cursor initially generated JUnit 4 annotations (`@Before`, `@Test` from `org.junit`) while `pom.xml` declared JUnit 5 (Jupiter).  
**Resolution:** Refined prompt to include `@pom.xml` and explicitly request `org.junit.jupiter.api.*`. Replaced `@Before` with `@BeforeEach`, `@Test` imports from Jupiter.  
**Prompt change:** Added *"Match @pom.xml JUnit 5 dependencies"* to every test-generation prompt.

### Error: Mockito not on classpath
**Symptom:** `package org.mockito does not exist` in `AuthServiceTest`  
**Cause:** First `pom.xml` draft included JUnit but omitted `mockito-core` test scope dependency.  
**Resolution:** Added to `pom.xml`:
```xml
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>5.11.0</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-junit-jupiter</artifactId>
  <version>5.11.0</version>
  <scope>test</scope>
</dependency>
```
**Prompt change:** Referenced `@pom.xml` before generating any test that uses `@Mock` or `@ExtendWith(MockitoExtension.class)`.

---

## 5.2 Test Failures During TDD

### Failure: AuthService login returned null instead of exception
**Test:** `AuthServiceTest.login_withInvalidPassword_throwsException`  
**Root cause:** AI implemented `login()` returning `null` on failure; test expected `AuthenticationException`.  
**Fix:** Standardized on exceptions for failure paths; updated `AuthService.login()` to throw `AuthenticationException` for bad credentials.  
**Lesson:** Specify expected failure mechanism (exception vs Optional vs null) in the test prompt.

### Failure: Inventory quantity went negative on fulfill
**Test:** `InventoryServiceTest.fulfillOrder_insufficientStock_throwsException`  
**Root cause:** `fulfillOrder` deducted quantity before checking sufficiency.  
**Fix:** Added guard clause at start of method:
```java
if (record.getQuantity() < requestedQty) {
    throw new InsufficientStockException(...);
}
record.decreaseQuantity(requestedQty);
```
**Prompt change:** Added *"reject insufficient stock before mutating state"* to inventory prompts.

### Failure: Restock task created when quantity equals minimum
**Test:** `RestockServiceTest.noTaskWhenQuantityAtMinimum`  
**Root cause:** Threshold check used `<= minimum` instead of `< minimum`.  
**Fix:** Changed condition to `quantity < minimumThreshold` per SRS (*"below minimum required"*).  
**Lesson:** Quote exact acceptance criteria wording in prompts to avoid off-by-one logic errors.

### Failure: Mockito stubbing not used (UnnecessaryStubbingException)
**Test:** Multiple tests in `RestockServiceTest`  
**Root cause:** `@BeforeEach` stubbed repository methods not used in every test.  
**Fix:** Moved stubs into individual test methods or used `lenient()` only where shared setup was required.  
**Prompt change:** Requested *"stub only what each test needs; avoid shared @BeforeEach mocks unless all tests use them"*.

---

## 5.3 Integration / Maven Issues

### Error: `mvn test` — wrong Java version
**Symptom:** `release version 17 not supported`  
**Cause:** Local JDK 11; project targets Java 17 per SRS.  
**Resolution:** Set `JAVA_HOME` to JDK 17; confirmed `<maven.compiler.release>17</maven.compiler.release>` in `pom.xml`.

### Error: Surefire does not discover tests
**Symptom:** `Tests run: 0`  
**Cause:** Test classes were not named `*Test.java` or were outside `src/test/java`.  
**Resolution:** Renamed files to Maven Surefire convention; verified package structure matches main code.

---

## 5.4 AI-Specific Issues

| Issue | How it appeared | Mitigation |
|-------|-----------------|------------|
| Scope creep | AI added SQLite JDBC in Demo 1 | Added *"in-memory repositories only"* constraint |
| Duplicate code | Same validation in User and Item | Used refactor prompt (10A) after tests green |
| Wrong layer | AI put business logic in Repository | Prompt: *"repositories are persistence only; logic in Service"* |
| Hallucinated classes | Referenced `StockMovementService` not in SRS | Attached story file with exact class list |
| Test + impl together | Skipped red phase of TDD | Split into two prompts: tests first, impl second |

---

## 5.5 Debugging Prompt That Worked Best

When a test failed after a change, this prompt produced the fastest fix:

```
@RestockServiceTest.java is failing on testCompleteTaskUpdatesInventory.
Stack trace: [paste trace]

Debug systematically:
1. likely root cause
2. exact code region (file + method)
3. smallest fix only
4. do not edit unrelated files

Run mvn test -Dtest=RestockServiceTest after the fix.
```

This outperformed vague prompts like *"fix my tests"* because it forced stack-trace-driven analysis and scoped edits.
