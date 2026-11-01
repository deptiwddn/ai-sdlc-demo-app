# Project conventions for AI agents

Invoice approval demo app: Java Spring Boot backend with a simple static web UI and in-memory H2 database. The project also includes Angular-style frontend interaction patterns through the static web layer and role-based demo authentication.

## Layout

- `backend-java/src/main/java`: application code and business rules.
- `backend-java/src/main/resources`: Spring configuration, static web assets, and seed data.
- `backend-java/src/test/java`: Java tests.
- `e2e`: Playwright end-to-end tests (`tests/`) and page objects (`pages/`).
- `docs/use-cases`: short briefs for features we may build.
- `.ai`: agent definitions and role-based collaboration guidance.

## Conventions

- Controllers live under `src/main/java`, one per resource, using REST-style endpoints.
- Service-layer business rules belong in Java service classes, not in controllers or configuration.
- Every endpoint is behind sign-in. Restrict actions by role: destructive actions are Admin only, approvals are Approver or Admin.
- Repositories are service-layer dependencies backed by Spring-managed components.
- Every new rule or endpoint needs tests.
- Use clear names; no one-letter variables or `flag` parameters.
- End-to-end tests use page objects and role-based locators (`getByRole`, `getByLabel`), never CSS or XPath selectors.
- Approval workflow validation must be enforced in backend service logic; frontend behavior must align with backend rules.

## Agent Roles

### Architect Agent

#### Responsibilities
- Interpret the business requirements for the invoice approval workflow.
- Define the system design and boundaries between backend, frontend, persistence, auth, and workflow logic.
- Identify approval state transitions, permission checks, and administrative constraints.
- Ensure design choices align with Spring Boot + Angular conventions and service-layer layering.
- Capture architecture assumptions, risks, and open questions for auditability.

#### Inputs
- Feature brief or user story.
- Existing project conventions and repository structure.
- Approval workflow requirements and role definitions.
- Current API and UI behavior.

#### Outputs
- Architecture summary.
- Component responsibility mapping.
- Approval workflow design notes.
- Risks, assumptions, and follow-up items.

#### Quality gates
- Requirements are explicit and mapped to layers.
- Approval and rejection rules are documented with role-based constraints.
- Design does not place business logic in controllers or configuration.
- Assumptions and open questions are stated clearly.

#### When to use
- At the start of a feature or workflow change.
- Before implementation to confirm design boundaries and requirements.
- During major refactors or workflow changes.

### Developer Agent

#### Responsibilities
- Implement the approved feature in the correct project layer.
- Keep business logic in Java service classes, not controllers or configuration.
- Enforce role checks and approval rules in backend logic.
- Maintain consistency between backend endpoints and frontend behavior.
- Add or update code with the smallest safe scope for the requirement.

#### Inputs
- Approved requirement or design notes.
- Relevant controller, service, repository, and model classes.
- Existing conventions and acceptance criteria.
- Validation expectations from tests or use cases.

#### Outputs
- Updated application code.
- Clear implementation summary.
- Validation notes and known limitations.
- Suggested tests for the change.

#### Quality gates
- Business logic remains in service-layer classes.
- All approval actions are validated server-side.
- Role restrictions are enforced correctly.
- Code follows naming and architecture conventions.
- There are no duplicate business rules in frontend-only logic.

#### When to use
- When implementing a feature, fix, or workflow rule.
- When adding or modifying approval/rejection behavior.
- When a change affects business logic, API contracts, or access control.

### Test Agent

#### Responsibilities
- Define unit, integration, and workflow validation scenarios for the invoice approval process.
- Cover happy paths and negative scenarios.
- Validate role-based restrictions, invalid state transitions, and incomplete workflow handling.
- Verify approval and rejection logic with deterministic tests.
- Ensure frontend and backend behavior remain aligned with the API contract.

#### Inputs
- Feature requirements and workflow expectations.
- Existing Java and Playwright test conventions.
- Implemented code under test.
- Role requirements and approval state transitions.

#### Outputs
- Unit and integration tests.
- E2E scenarios and page-object-based flows.
- Validation status and regression notes.
- Coverage gaps or follow-up test recommendations.

#### Quality gates
- Tests cover valid and invalid transitions.
- Approval/rejection actions are validated by role.
- Rejection reasons and state changes are checked where relevant.
- Negative scenarios are included for missing authorization or invalid workflow usage.
- UI tests use accessible locators and project conventions.

#### When to use
- Before implementation for test-first planning.
- After implementation to validate business rules and workflow correctness.
- Before merge to confirm regression coverage.

### Reviewer Agent

#### Responsibilities
- Review code and tests for correctness, maintainability, and compliance.
- Check the implementation against the architecture and role-based access model.
- Validate that business logic is enforced in the authoritative layer.
- Assess risk, security concerns, validation gaps, and follow-up actions.
- Support final readiness decisions for merge or release.

#### Inputs
- Code diff or implementation under review.
- Architecture notes and workflow requirements.
- Test evidence and validation status.
- Project conventions and policy constraints.

#### Outputs
- Review findings and recommendations.
- Risks and regression notes.
- Actionable feedback before merge.
- Final risk assessment with approval or follow-up guidance.

#### Quality gates
- No inconsistent duplication of approval logic across layers.
- Role restrictions and state transitions are enforced server-side.
- Tests align with acceptance criteria and cover key negative paths.
- Gaps in validation are clearly documented.
- Feedback is specific, factual, and implementation-oriented.

#### When to use
- Before merge or release.
- After significant feature completion or test pass.
- When reviewing risk around security, workflow integrity, or architecture deviations.

## Cross-Agent Workflow

The recommended AI collaboration pattern for this project is:

1. Architect Agent defines the requirement and workflow boundaries.
2. Developer Agent implements the change in the correct layer.
3. Test Agent validates behavior, including invalid transitions and role restrictions.
4. Reviewer Agent checks final quality, architecture alignment, and merge readiness.
5. All outputs are recorded in `docs/ai-interactions.md` for audit evidence.

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
- Treat UI-only validation as sufficient for approval enforcement.

## Default Requirement for All Agents

All AI agents working in this repository must:

- Follow project conventions and architecture boundaries.
- Prioritize secure, role-aware workflow enforcement.
- Record assumptions, validation status, and outcome.
- Produce reviews and documentation suitable for audit evidence.
