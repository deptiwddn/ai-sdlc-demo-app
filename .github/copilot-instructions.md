# GitHub Copilot Instructions for This Repository

These instructions apply to all AI-assisted work in this repository and are designed to support auditability, traceability, and professional SDLC practices.

## Core Expectations

When generating code, analysis, tests, or review feedback, GitHub Copilot must:

- Always provide a concise summary of the generated work.
- List all impacted files and clearly identify what changed.
- Suggest relevant tests, including validation strategy and any gaps that remain.
- Generate a markdown log entry that can be appended to `docs/ai-interactions.md`.
- Follow spec-driven development practices.
- Record architecture, implementation, testing, and review activities in a way that supports audit review.
- Be explicit about assumptions, limitations, and follow-up actions.
- Avoid making claims about execution or correctness without noting validation status.

## Spec-Driven Development

GitHub Copilot must operate in a spec-driven workflow:

1. Confirm the relevant requirement or feature context before making changes.
2. Identify the expected behavior, constraints, and acceptance criteria.
3. Map the work to the appropriate application layer and project conventions.
4. Implement only the required change and avoid unrelated scope.
5. Validate the result with tests or explicit reasoning when tests are not available.
6. Record the design, implementation, and verification details for auditability.

Where a feature or change is not fully specified, the assistant should call out the missing details and make the smallest safe assumption rather than inventing requirements.

## Architecture Awareness

For a Spring Boot + Angular invoice approval application, GitHub Copilot must preserve the intended system boundaries and architectural layering:

- Keep backend business logic in service-layer classes, not in controllers or configuration.
- Maintain clear separation between REST API controllers, application services, domain logic, persistence, and security/authentication layers.
- Keep frontend behavior aligned with the API contract and domain workflow without duplicating backend business rules.
- Preserve clean data flow from UI interactions to backend endpoints and database persistence.
- Respect role-based access control and approval enforcement in both backend and frontend logic.
- Prefer secure patterns, validate input, and avoid unsafe assumptions about request data.

## Test-First Mindset

GitHub Copilot must favor a test-first workflow for business logic and workflow changes:

- Write or update tests before implementation when practical.
- Define the expected behavior in unit tests for services and domain rules.
- Add or refine integration tests for API and workflow validation.
- Add end-to-end tests for approval scenarios that exercise the user flow.
- Cover both happy paths and negative scenarios.
- Validate role-based restrictions, invalid transitions, and incomplete state handling.
- Keep tests readable, deterministic, and aligned to the project’s testing conventions.

## Approval Workflow Rules

The invoice approval workflow must be treated as a business-critical process. GitHub Copilot should ensure the implementation follows these rules:

- Invoice approval and rejection actions must be validated by backend service logic.
- Only authorized roles may approve or reject invoices.
- Destructive or administrative actions must be restricted to Admin-level access when required.
- State transitions must be explicitly validated; invalid transitions must be rejected.
- Rejection should include a clear reason when applicable.
- Approval state changes should be consistent across backend, frontend, and persisted data.
- The UI should not be the only enforcement point; server-side validation must remain authoritative.

## Requirement Analysis

Before making changes, GitHub Copilot must:

1. Confirm the relevant requirement, user story, or feature context.
2. Identify the expected behavior, constraints, and acceptance criteria.
3. Map the work to the correct application layer and repository conventions.
4. Implement only the required change and avoid unrelated scope.
5. Validate the result with tests or explicit reasoning when tests are unavailable.
6. Record the design, implementation, and validation details for audit review.

If requirements are ambiguous, explicitly call out the missing detail and make the smallest safe assumption rather than inventing business behavior.

## Mutation Testing Guidance

When relevant, GitHub Copilot should consider mutation testing as a quality safeguard for critical logic:

- Prefer tests that would fail if approval logic is mutated incorrectly.
- Ensure tests detect common defects such as reversed conditions, missing validation, incorrect role checks, or wrong state transitions.
- For financial and approval workflows, include tests that exercise boundary conditions and invalid paths.
- Use mutation testing feedback to strengthen tests rather than increase test count without quality.
- If mutation testing tooling is not configured, explicitly call out the gap and recommend how to add it.

## Logging AI Interactions

GitHub Copilot must maintain a record of significant AI-assisted work in `docs/ai-interactions.md`.

Each AI interaction entry should include:
- Date/Time
- AI Tool Used
- Phase
- Prompt Provided
- AI Response Summary
- Action Taken
- Files Modified
- Outcome

Use the project log template when capturing work:

```markdown
### <Short Activity Title>
- Date/Time: YYYY-MM-DD HH:MM (UTC or local time)
- AI Tool Used: GitHub Copilot Chat / Copilot Edits / Copilot Review / Other
- Phase: Analysis / Design / Development / Testing / Review
- Prompt Provided: <Summary of the task, request, or question given to the AI>
- AI Response Summary: <What the AI produced or suggested>
- Action Taken: <How the team used the response in implementation, review, or validation>
- Files Modified: <List of files changed or reviewed, if any>
- Outcome: <Result, including validation status, deployment status, or follow-up actions>
```

When AI work is purely analytical, the `Files Modified` field may be recorded as `None (analysis-only)`.

## PR Review Expectations

Before finalizing work, GitHub Copilot should review the change as if preparing for merge:

- Confirm the code remains consistent with architecture boundaries.
- Check that business logic is not duplicated across frontend and backend in conflicting ways.
- Ensure role restrictions and approval rules are enforced in the authoritative layer.
- Review test coverage for both positive and negative flows.
- Flag security, validation, and regression risks clearly.
- Recommend follow-up actions when the issue is out of scope or requires product clarification.
- Provide professional, actionable feedback rather than vague criticism.

## Required Output Pattern

Every AI-generated response should include the following sections when applicable:

### Summary
Provide a brief description of the work performed, the problem addressed, and the relevant business or technical context.

### Impacted Files
List each relevant file touched or reviewed, including:
- application code
- tests
- configuration
- documentation
- static assets

### Tests
Describe:
- tests added or updated
- tests recommended for future coverage
- validation status (passed, failed, not run, partial)
- edge cases or negative scenarios considered

### Architecture / Design Notes
When relevant, summarize:
- system architecture impact
- component responsibilities
- technical constraints
- security, role, or data-flow considerations
- whether the implementation follows project conventions

### Review / Risk Notes
Document:
- assumptions made
- follow-up actions needed
- known limitations
- potential regressions or validation gaps

### AI Interaction Log Entry
Append a markdown entry to `docs/ai-interactions.md` using the project log format.

## Auditability Requirements

AI-assisted work must be easy to review and defend. The assistant should therefore:

- document rationale and decisions
- distinguish between analysis, design, implementation, testing, and review work
- identify exact files involved
- record validation evidence, not just intent
- avoid vague language such as "fixed it" without describing what changed and why
- keep records factual, concise, and professional

## Repository Conventions

Follow the project conventions defined in `AGENTS.md` and `CLAUDE.md`:

- business logic belongs in service classes, not controllers or configuration
- endpoints are behind sign-in and restricted by role
- destructive actions are Admin-only
- approvals are Approver or Admin
- repositories remain service-layer dependencies backed by Spring-managed components
- new rules and endpoints require tests
- use clear names and avoid one-letter variables
- prefer accessible UI selectors such as `getByRole` and `getByLabel`

## Activity Categories

The assistant should explicitly label the work category when relevant:

- Architecture / Analysis: requirements interpretation, system design, workflow review, technical planning
- Development: code generation, refactoring, business logic implementation
- Testing: unit test creation, integration coverage, validation planning, defect reproduction
- Review: code review feedback, quality assessment, security review, risk analysis

## Final Instruction

Before concluding any AI-assisted task, ensure the response includes:

- a clear work summary
- impacted file list
- suggested or executed tests
- any architecture/design implications
- a markdown entry suitable for `docs/ai-interactions.md`
- validation status and any follow-up actions

This ensures the generated work remains traceable, reviewable, and aligned with audit-friendly AI-assisted SDLC practices.
