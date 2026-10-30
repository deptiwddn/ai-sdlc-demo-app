# Invoice Approval Demo App

A small invoice approval app used for live AI-in-SDLC demos. It is implemented as a Java Spring Boot application with an in-memory H2 database and a plain web UI.

## Quick start

Run the Spring Boot app:

```bash
cd backend-java
mvn spring-boot:run
```

Then open http://localhost:8080.

Sign in with one of the demo accounts below:

| User | Password | Role |
| --- | --- | --- |
| `demo.approver` | `ChangeMe123!` | Approver |
| `demo.admin` | `ChangeMe123!` | Admin |

These are demo accounts for the local app only; they are not real credentials.

## What it does

1. Create an invoice. It starts in Draft.
2. Submit it for approval.
3. An approver or admin approves or rejects it with a reason.

Invoices move through Draft, PendingApproval, then Approved or Rejected.

## App behavior and security

- Every API endpoint is behind sign-in.
- Destructive actions require the Admin role.
- Approval actions require the Approver or Admin role.
- Business logic is kept in the Java service layer, not in controllers or configuration.

## Tests

Run the Java test suite:

```bash
cd backend-java
mvn test
```

## Project layout

```text
backend-java/
  src/main/java/       Spring Boot app code
  src/main/resources/  app config and static web assets
  src/test/java/       Java tests
  pom.xml              Maven build configuration

Docs and demo artifacts are also kept in the repository root.
```

## Use cases

- [UC-1 Bulk invoice approval](docs/use-cases/UC-1-bulk-approval.md)
- [UC-2 Partial approval](docs/use-cases/UC-2-partial-approval.md)

## Notes

- Do not commit real credentials, tokens, or customer data.
- Keep business logic in the service layer and out of the controller layer.
