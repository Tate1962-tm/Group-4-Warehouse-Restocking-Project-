# ZIP Archive — Submission Folder Guide

Place the following into your ZIP for the Extra Credit assignment.

## Required Structure

```
Group4-AWRS-ExtraCredit-Cursor/
│
├── EXTRA_CREDIT_REPORT.pdf          ← Export docs/extra-credit/EXTRA_CREDIT_REPORT.md
│
├── docs/
│   ├── extra-credit/                ← Section markdown files (optional if in PDF)
│   ├── stories/
│   │   ├── STORY-01-user-authentication.md
│   │   ├── STORY-02-item-catalog-locations.md
│   │   ├── STORY-03-inventory-tracking.md
│   │   └── STORY-04-automated-restocking.md
│   └── design/
│       ├── demo1-class-diagram.mmd
│       ├── demo2-sequence-receive-shipment.mmd
│       └── demo3-use-case-restocking.mmd
│
├── src/
│   ├── main/java/                   ← Your production Java files
│   │   ├── User.java
│   │   ├── Item.java
│   │   ├── WarehouseLocation.java
│   │   ├── InventoryRecord.java
│   │   ├── RestockTask.java
│   │   ├── AuditLog.java
│   │   ├── AuthService.java
│   │   ├── InventoryService.java
│   │   ├── RestockService.java
│   │   ├── UserRepository.java
│   │   ├── ItemRepository.java
│   │   ├── InventoryRepository.java
│   │   ├── RestockTaskRepository.java
│   │   └── AuditLogRepository.java
│   └── test/java/                   ← Your test Java files
│       ├── UserTest.java
│       ├── ItemTest.java
│       ├── WarehouseLocationTest.java
│       ├── AuthServiceTest.java
│       ├── InventoryServiceTest.java
│       └── RestockServiceTest.java
│
├── pom.xml
└── README.md
```

## Steps to Prepare Submission

1. **Copy your Java source files** into `src/main/java/` and tests into `src/test/java/` (preserve your package folders).
2. **Export PDF** — open `docs/extra-credit/EXTRA_CREDIT_REPORT.md` in VS Code/Cursor and use *Markdown PDF* extension, or paste sections into Google Docs/Word and export.
3. **Render Mermaid diagrams** — paste `.mmd` content into https://mermaid.live and save PNGs for the PDF if required.
4. **Verify tests pass:** `mvn test`
5. **Create ZIP** from the folder root.
6. **Complete Canvas survey** (Section 7, 10 pts).

## Demo Reference

| Demo | Test Files | Focus |
|------|------------|-------|
| Demo 1 | UserTest, ItemTest, WarehouseLocationTest, AuthServiceTest | Domain models + authentication |
| Demo 2 | InventoryServiceTest | Inventory receive/fulfill/adjust |
| Demo 3 | RestockServiceTest | Automated restock engine |
