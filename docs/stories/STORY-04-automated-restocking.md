# STORY-04: Automated Restocking Engine

## User Story
As a warehouse manager, I want the system to automatically detect low stock and generate prioritized restock tasks so that workers can replenish inventory before stockouts occur.

## Acceptance Criteria
1. When quantity falls below minimum threshold, a restock task is created automatically.
2. Task priority is determined by distance from reorder point and estimated stockout time.
3. Restock tasks follow lifecycle: Created → Assigned → In Progress → Completed.
4. Completing a restock task updates inventory at the target location.
5. Batch processing can evaluate and create tasks for many items at once.
6. Predictive module uses moving average to suggest proactive restocking.

## Backend Tasks
- Create `RestockTask` model with status enum and priority field.
- Implement `RestockService` with threshold evaluation, task generation, and completion.
- Implement `RestockTaskRepository`.
- Add unit tests in `RestockServiceTest.java`.

## Frontend Tasks (Iteration 2–3+)
- Restock task queue dashboard.
- Task assignment and completion forms.

## Demo Mapping
**Demo 3** — `RestockServiceTest.java`
