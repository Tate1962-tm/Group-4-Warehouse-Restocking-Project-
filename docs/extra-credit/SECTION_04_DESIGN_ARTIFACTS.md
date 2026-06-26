# Section 4: Generated Design Artifacts, Code & Test Cases

## 4.1 Design Artifacts Included in This Report

| Artifact | File | Demo | Description |
|----------|------|------|-------------|
| User story cards | `docs/stories/STORY-01` through `STORY-04` | 1–3 | Acceptance criteria and task breakdown |
| Class diagram (Mermaid) | `docs/design/demo1-class-diagram.mmd` | 1 | User, Item, AuthService, repositories |
| Sequence diagram (Mermaid) | `docs/design/demo2-sequence-receive-shipment.mmd` | 2 | Receive shipment workflow |
| Use case diagram (Mermaid) | `docs/design/demo3-use-case-restocking.mmd` | 3 | Restock engine actors and use cases |

### Demo 1 — Class Diagram (excerpt)

See full file in ZIP: `docs/design/demo1-class-diagram.mmd`

Core relationships:
- `User` has `Role` enum (ADMIN, MANAGER, WORKER)
- `AuthService` depends on `UserRepository`
- `Item` and `WarehouseLocation` are standalone domain models for Demo 1

### Demo 2 — Receive Shipment Sequence

See full file in ZIP: `docs/design/demo2-sequence-receive-shipment.mmd`

Flow: Worker → InventoryService → InventoryRepository → AuditLogRepository

### Demo 3 — Restock Use Cases

See full file in ZIP: `docs/design/demo3-use-case-restocking.mmd`

Actors: Manager, Worker, System Engine  
Use cases: Evaluate Thresholds, Generate Task, Assign, Complete, Batch Process

---

## 4.2 Generated / Modified Source Code

Attach these files from your project in the ZIP under `submission/src/`:

### Model Layer (Demo 1)
| File | Purpose |
|------|---------|
| `User.java` | Username, password hash, role |
| `Item.java` | SKU, description, supplier, UOM |
| `WarehouseLocation.java` | Hierarchical location tree |

### Service Layer (Demos 1–3)
| File | Purpose |
|------|---------|
| `AuthService.java` | Login and role-based access |
| `InventoryService.java` | Receive, fulfill, adjust inventory |
| `RestockService.java` | Threshold evaluation and task lifecycle |

### Persistence Layer
| File | Purpose |
|------|---------|
| `UserRepository.java` | User CRUD |
| `ItemRepository.java` | Item CRUD |
| `InventoryRepository.java` | InventoryRecord storage |
| `RestockTaskRepository.java` | Restock task storage |
| `AuditLogRepository.java` | Audit trail storage |

### Additional Models (Demos 2–3)
| File | Purpose |
|------|---------|
| `InventoryRecord.java` | Item quantity at location |
| `AuditLog.java` | Transaction audit entry |
| `RestockTask.java` | Restock task with status and priority |

---

## 4.3 Generated / Modified Test Cases

| Test File | Demo | Scenarios Covered |
|-----------|------|-------------------|
| `UserTest.java` | 1 | Valid user, empty username, role assignment |
| `ItemTest.java` | 1 | Valid item, duplicate SKU, validation |
| `WarehouseLocationTest.java` | 1 | Hierarchy, path, invalid name |
| `AuthServiceTest.java` | 1 | Login success/failure, role checks |
| `InventoryServiceTest.java` | 2 | Receive, fulfill, insufficient stock, audit log |
| `RestockServiceTest.java` | 3 | Threshold trigger, complete task, batch, priority |

---

## 4.4 Build Configuration

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build; JUnit 5 + Mockito dependencies |
| `README.md` | Project overview and run instructions |

### Run all tests
```bash
mvn test
```

---

## 4.5 ZIP Archive Folder Layout

```
Group4-AWRS-ExtraCredit-Cursor/
├── PDF_REPORT.pdf                    ← Sections 3–6 from this documentation
├── docs/
│   ├── stories/                      ← STORY-01 through STORY-04
│   └── design/                       ← Mermaid diagrams
├── src/
│   ├── main/java/.../                ← All production Java files listed above
│   └── test/java/.../                ← All test Java files listed above
├── pom.xml
└── README.md
```

**Note:** Copy your actual `.java` files into `src/` before zipping. The PDF should embed or screenshot key diagrams and representative test/code snippets.
