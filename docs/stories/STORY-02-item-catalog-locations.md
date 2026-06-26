# STORY-02: Item Catalog and Warehouse Location Definitions

## User Story
As a warehouse manager, I want to define items and hierarchical auth locations so that inventory can be tracked at specific storage points.

## Acceptance Criteria
1. Items shall have SKU, description, supplier, and unit of measure.
2. Duplicate SKUs are rejected.
3. Warehouse locations follow a hierarchical structure (warehouse → aisle → shelf → bin).
4. Admin and Manager roles can create locations; Workers cannot.
5. Each location has a unique identifier within its warehouse.

## Backend Tasks
- Create `Item` model with validation for required fields.
- Create `WarehouseLocation` model with parent/child hierarchy support.
- Implement `ItemRepository` and location persistence.
- Add unit tests in `ItemTest.java` and `WarehouseLocationTest.java`.

## Frontend Tasks (Iteration 1+)
- Item catalog CRUD form in JavaFX.
- Tree view for warehouse location hierarchy.

## Demo Mapping
**Demo 1** — `ItemTest.java`, `WarehouseLocationTest.java`
