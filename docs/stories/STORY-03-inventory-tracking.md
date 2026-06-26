# STORY-03: Real-Time Inventory Tracking

## User Story
As a warehouse worker, I want the system to track item quantities at each location so that receiving, fulfillment, and adjustments update counts accurately.

## Acceptance Criteria
1. Receiving a shipment increases quantity at the specified location.
2. Order fulfillment deducts quantity and rejects insufficient stock.
3. Manual adjustments require a reason code and Manager/Admin approval.
4. Each transaction creates an `AuditLog` entry.
5. Inventory queries return current quantity per item per location.

## Backend Tasks
- Create `InventoryRecord` model linking item, location, and quantity.
- Create `AuditLog` model for transaction history.
- Implement `InventoryService` with receive, fulfill, and adjust operations.
- Implement `InventoryRepository` and `AuditLogRepository`.
- Add unit tests in `InventoryServiceTest.java`.

## Frontend Tasks (Iteration 2+)
- Receiving shipment form.
- Order fulfillment form with stock validation feedback.

## Demo Mapping
**Demo 2** — `InventoryServiceTest.java`
