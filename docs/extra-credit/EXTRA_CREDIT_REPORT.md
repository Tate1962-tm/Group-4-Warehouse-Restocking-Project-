# AWRS Extra Credit — Cursor IDE Report (Sections 3–7)

**Course:** CS 4398 Software Engineering  
**Project:** Automated Warehouse Restock System (AWRS) — Group 4  
**Members:** Aya Alkhatib, Jade Wilson, Tatenda Machirori, Zainab Khan  
**Tool:** Cursor IDE  

---

## Table of Contents

1. [Section 3 — Prompts Used](#section-3--prompts-used)
2. [Section 4 — Design Artifacts, Code & Tests](#section-4--generated-design-artifacts-code--test-cases)
3. [Section 5 — Errors & Debugging](#section-5--errors--debugging-notes)
4. [Section 6 — Reflection](#section-6--reflection)
5. [Section 7 — Survey](#section-7--survey-required)

> **PDF instructions:** Export this file (or combine the section files below) to PDF for submission. Embed Mermaid diagrams as rendered images or screenshots from [mermaid.live](https://mermaid.live).

---

## Section 3 — Prompts Used

See full detail: [`SECTION_03_PROMPTS_USED.md`](./SECTION_03_PROMPTS_USED.md)

**Summary:** Sixteen prompts across three demos, following the assignment's sample pattern (story analysis → design → plan → tests first → implementation → debug/refactor). All prompts reference AWRS story files and Java/Maven project structure.

**Demo mapping:**
| Demo | Stories | Tests | Services |
|------|---------|-------|----------|
| 1 | STORY-01, STORY-02 | UserTest, ItemTest, WarehouseLocationTest, AuthServiceTest | AuthService |
| 2 | STORY-03 | InventoryServiceTest | InventoryService |
| 3 | STORY-04 | RestockServiceTest | RestockService |

---

## Section 4 — Generated Design Artifacts, Code & Test Cases

See full detail: [`SECTION_04_DESIGN_ARTIFACTS.md`](./SECTION_04_DESIGN_ARTIFACTS.md)

**Design artifacts (in repo):**
- `docs/stories/STORY-01` through `STORY-04`
- `docs/design/demo1-class-diagram.mmd`
- `docs/design/demo2-sequence-receive-shipment.mmd`
- `docs/design/demo3-use-case-restocking.mmd`

**Code & tests (attach your Java files to ZIP):**
- **Models:** User, Item, WarehouseLocation, InventoryRecord, RestockTask, AuditLog
- **Services:** AuthService, InventoryService, RestockService
- **Repositories:** UserRepository, ItemRepository, InventoryRepository, RestockTaskRepository, AuditLogRepository
- **Tests:** UserTest, ItemTest, WarehouseLocationTest, AuthServiceTest, InventoryServiceTest, RestockServiceTest
- **Config:** pom.xml, README.md

---

## Section 5 — Errors & Debugging Notes

See full detail: [`SECTION_05_ERRORS_DEBUGGING.md`](./SECTION_05_ERRORS_DEBUGGING.md)

**Highlights:**
- JUnit 4/5 import mismatch — fixed by referencing pom.xml in prompts
- Mockito missing from pom.xml — added mockito-core and mockito-junit-jupiter
- Threshold off-by-one in RestockService — clarified SRS wording in prompt
- Insufficient stock allowed negative quantity — guard before mutation
- AI scope creep (SQLite too early) — constrained to in-memory repos

---

## Section 6 — Reflection

See full detail: [`SECTION_06_REFLECTION.md`](./SECTION_06_REFLECTION.md)

**Highlights:**
- Story files + `@` references worked best for contextual generation
- Splitting "tests only" and "impl only" prompts preserved TDD
- AI sped up boilerplate; humans still needed for requirements and review
- Compared to pre-AI submission: faster tests, but required debugging AI inconsistencies

---

## Section 7 — Survey (Required)

Complete the **Canvas survey** for this extra credit assignment (**10 points**).

You must finish the survey in addition to submitting the PDF report and ZIP archive.

---

## ZIP Submission Checklist

- [ ] PDF report (Sections 3–6 from this documentation)
- [ ] `docs/stories/` — all four story files
- [ ] `docs/design/` — all Mermaid diagrams
- [ ] `src/main/java/` — all production Java files
- [ ] `src/test/java/` — all test Java files
- [ ] `pom.xml`
- [ ] `README.md`
- [ ] Canvas survey completed

```bash
# Example: create ZIP from project root
zip -r Group4-AWRS-ExtraCredit-Cursor.zip \
  docs/ src/ pom.xml README.md
```
