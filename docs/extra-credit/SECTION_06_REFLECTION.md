# Section 6: Reflection

## What Worked Well

**Story-driven prompts with `@` file context.** Referencing `@docs/stories/STORY-03-inventory-tracking.md` and the SRS kept generated code aligned with acceptance criteria. Cursor pulled in the right domain terms (SKU, reorder point, audit log) without repeating the full spec in every prompt.

**Strict TDD split (tests first, then implementation).** Separating prompts into *"write failing tests only"* and *"implement minimum code to pass"* produced cleaner red-green cycles. When both were requested in one prompt, the AI often skipped the failing-test phase and wrote passing code immediately, which weakened the TDD demonstration.

**Mockito for service-layer isolation.** Using `@Mock` for repositories in `AuthServiceTest`, `InventoryServiceTest`, and `RestockServiceTest` let us test business logic without SQLite or in-memory database setup. This matched our Demo 1–3 scope and kept tests fast.

**Incremental demos mapped to iterations.** Organizing work as Demo 1 (models + auth), Demo 2 (inventory), Demo 3 (restock) mirrored the SRS iteration plan and made class-session deliverables manageable.

**Mermaid design artifacts before code.** Generating class, sequence, and use case diagrams from story files gave the team a shared visual reference and reduced rework when implementing services.

---

## What Did Not Work Well

**Vague prompts produced scope creep.** Early prompts like *"implement the inventory module"* led to JDBC wiring, DTO classes, and GUI stubs we did not need for unit-test demos. Constraints had to be repeated every prompt.

**Inconsistent failure handling.** Without explicit instructions, the AI mixed null returns, empty Optionals, and exceptions across services. We spent time standardizing after tests were written.

**JUnit version confusion.** The first test generation used JUnit 4 patterns despite JUnit 5 in `pom.xml`. Always attaching the build file prevented this in later demos.

**Over-stubbing in shared setup.** `@BeforeEach` Mockito stubs caused `UnnecessaryStubbingException` when not every test used every mock. Per-test stubbing was more verbose but more reliable.

**SRS vs implementation stack drift.** Our original SRS mentioned C++/Qt; the Java/JavaFX stack in Assignment 1 PDF was the actual target. We had to clarify the stack in prompts to avoid C++-style suggestions.

---

## Strategies That Improved Output

1. **Attach constraints as a bullet list** — e.g., *no GUI, in-memory only, JUnit 5, one class session*.
2. **Name exact test methods and scenarios** in the prompt so the AI does not invent extra cases.
3. **One story, one service, one prompt chain** — avoid multi-story prompts that blur boundaries.
4. **Paste stack traces into debug prompts** — dramatically improved fix accuracy.
5. **Review AI output before running tests** — catch wrong imports and extra files early.
6. **Iterate prompts in a table** (see Section 3) — document what changed and why for the report.

---

## What I Learned About IDE + AI for Software Development

AI in Cursor is most effective as a **structured accelerator**, not a replacement for requirements thinking. It generates boilerplate (models, tests, repositories) quickly, but it does not reliably enforce architectural boundaries or TDD discipline unless the developer specifies both.

The **`@` reference system** is the highest-value feature for course projects: pointing at story markdown, `pom.xml`, or an existing test file grounds the model in *our* project instead of generic warehouse or e-commerce templates.

**Human review remains essential** for edge cases (threshold comparisons, negative inventory, role checks) and for keeping the codebase consistent across three demos contributed by different team members.

Pairing AI generation with **`mvn test` after every prompt** created a tight feedback loop similar to pair programming — the compiler and tests catch AI mistakes faster than manual reading alone.

---

## Comparison: With AI vs Without AI (Initial Submitted Code)

| Aspect | Initial submission (no AI) | With Cursor AI assistance |
|--------|---------------------------|---------------------------|
| Test coverage | Manual tests; slower to write; fewer edge cases | More scenarios generated; fixed after Mockito/JUnit issues |
| Design docs | UML in draw.io; manual updates | Mermaid from stories; faster iteration |
| Consistency | Varied naming across team files | Prompts enforced shared patterns (exceptions, repository injection) |
| Time to first green test | Longer; hand-written boilerplate | Shorter for models and CRUD-style services |
| Architectural purity | Team discussions caught layer violations | AI sometimes put logic in wrong layer; caught in review |
| Understanding | Deeper manual coding familiarity | Required reading AI output to truly understand generated code |

**Overall:** AI reduced time on repetitive code and test scaffolding. Requirements analysis, acceptance criteria validation, and final integration decisions still required human judgment. The best results came from **human-written stories + AI-generated tests/code + human verification**.

---

## Section 7: Survey Reminder

Complete the structured survey on **Canvas** (required — 10 pts).  
Link is provided by your instructor on the Extra Credit Assignment page.

Survey topics typically cover:
- Usefulness of AI-assisted IDE features
- Quality of generated code and tests
- Time saved vs time spent debugging AI output
- Comparison with traditional development workflow

**Action item:** Submit survey before the assignment deadline; mention which demos (1–3) you completed in Cursor.
