# Project conventions for AI agents

Invoice approval demo app: Java Spring Boot application with a simple static web UI and in-memory H2 database.

## Layout

- `backend-java/src/main/java`: application code and business rules.
- `backend-java/src/main/resources`: Spring configuration, static web assets, and seed data.
- `backend-java/src/test/java`: Java tests.
- `e2e`: Playwright end-to-end tests (`tests/`) and page objects (`pages/`).
- `docs/use-cases`: short briefs for features we may build.

## Conventions

- Controllers live under `src/main/java`, one per resource, using REST-style endpoints.
- Service-layer business rules belong in Java service classes, not in controllers or configuration.
- Every endpoint is behind sign-in. Restrict actions by role: destructive actions are Admin only, approvals are Approver or Admin.
- Repositories are service-layer dependencies backed by Spring-managed components.
- Every new rule or endpoint needs tests.
- Use clear names; no one-letter variables or `flag` parameters.
- End-to-end tests use page objects and role-based locators (`getByRole`, `getByLabel`), never CSS or XPath selectors.

## Commands

- Run the app: `cd backend-java && mvn spring-boot:run` then open http://localhost:8080
- Run the tests: `cd backend-java && mvn test`
- Run end-to-end tests: `cd e2e && npm install && npx playwright install chromium && npm test`

## Demo accounts (not real credentials)

- `demo.approver` / `ChangeMe123!` (role Approver)
- `demo.admin` / `ChangeMe123!` (role Admin)

## Never

- Commit real credentials, tokens or customer data.
- Add business logic to configuration classes or controllers.
