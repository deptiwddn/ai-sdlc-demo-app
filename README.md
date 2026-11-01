# Invoice Approval Demo App

A Spring Boot invoice approval application used for live AI-in-SDLC demos. It includes a Java backend, an in-memory H2 database, and a lightweight web UI that follows Angular-style interaction patterns. The current project includes the approval enhancement workflow, role-based permissions, Playwright validation, and mutation testing support.

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
| `demo.manager` | `ChangeMe123!` | Manager |
| `demo.director` | `ChangeMe123!` | Director |
| `demo.director2` | `ChangeMe123!` | Director |
| `demo.admin` | `ChangeMe123!` | Admin |

These are demo accounts for the local app only; they are not real credentials.

## What it does

1. Create an invoice. It starts in Draft.
2. Submit it for approval.
3. A role-appropriate user approves or rejects it with a reason.
4. High-value invoices above 500,000 require a second Director approval from a different user.
5. Invalid approval attempts do not mutate the invoice state.

Invoice flow includes Draft, PendingApproval, PendingSecondApproval, Approved, and Rejected.

## Approval workflow

The current business rules implemented are:

- Manager approval limit: up to 50,000
- Director approval limit: up to 500,000
- Invoice above 500,000 requires two Director approvals
- The second approver must be a different user
- Duplicate approvals by the same user are rejected
- Rejections require a reason and preserve the invoice state when invalid

## App behavior and security

- Every API endpoint is behind sign-in.
- Destructive actions require the Admin role.
- Approval actions are enforced in the Java service layer, not in the controller or UI.
- Approval logic is protected by role and workflow validation in the backend.
- Frontend behavior aligns with the backend business rules.

## Tests

Run the Java test suite:

```bash
cd backend-java
mvn test
```

Run the Playwright end-to-end suite:

```bash
cd e2e
npm install
npx playwright install chromium
npx playwright test
```

Run PIT mutation coverage:

```bash
cd backend-java
mvn org.pitest:pitest-maven:mutationCoverage
```

## Project layout

```text
backend-java/
  src/main/java/       Spring Boot app code and business rules
  src/main/resources/  app config, static web assets, seed data
  src/test/java/       Java tests
  pom.xml              Maven build configuration

e2e/
  tests/               Playwright end-to-end tests
  pages/               page objects for browser flows

openspec/
  changes/             OpenSpec requirement and task artifacts

docs/
  ai-interactions.md   AI activity logging
  implementation-plan.md
  risk-assessment.md
  pr-review-findings.md
  mutation-testing-before.md
  mutation-testing-after.md
```

## AI-assisted SDLC workflow

This repository demonstrates a spec-driven, AI-assisted SDLC workflow using:

- GitHub Copilot
- OpenSpec
- Spring Boot
- Playwright
- PIT mutation testing

The workflow includes requirement specification, implementation planning, development, testing, review, and audit logging.

## Notes

- Do not commit real credentials, tokens, or customer data.
- Keep business logic in the service layer and out of the controller layer.
- Maintain traceability from requirements through implementation, tests, and review evidence.
