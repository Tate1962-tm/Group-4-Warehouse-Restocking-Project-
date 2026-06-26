# STORY-01: User Authentication and Role-Based Access

## User Story
As a warehouse employee, I want to log in with my username and password so that I can access features allowed by my role (Admin, Manager, or Worker).

## Acceptance Criteria
1. Valid credentials authenticate successfully and return the user's role.
2. Invalid username or password returns an authentication failure.
3. Empty username or password is rejected with a validation error.
4. Only Admin users can create, edit, delete, and assign roles to other users.
5. Manager and Worker users cannot perform user-management operations.
6. All authentication attempts are logged for audit purposes.

## Backend Tasks
- Create `User` model with username, password hash, and `Role` enum (ADMIN, MANAGER, WORKER).
- Implement `UserRepository` for in-memory or SQLite persistence.
- Implement `AuthService` with login validation and role checks.
- Add unit tests in `AuthServiceTest` covering valid login, invalid credentials, and empty fields.

## Frontend Tasks (Iteration 1+)
- Create JavaFX login form with username/password fields.
- Display role-appropriate navigation after successful login.
- Show inline error messages for failed login attempts.

## Demo Mapping
**Demo 1** — `UserTest.java`, `AuthServiceTest.java`
