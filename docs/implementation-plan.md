# Implementation Plan

## Purpose
This plan defines the governance, AI workflow, and evidence practices for the invoice approval assignment. It creates the operating model for architecture analysis, development, testing, and review without modifying application code.

## Scope
In scope:
- AI agent role definitions and responsibilities
- SDLC governance and audit evidence
- OpenSpec planning artifacts for AI-assisted work
- Interaction logging standards
- Hook definitions for workflow stages
- Risk management and review controls

Out of scope:
- Business logic changes in the Spring Boot application
- UI or backend code refactors
- Database schema or service implementation work
- Production deployment tasks

## Repository Context
- Backend: Java Spring Boot with service-layer business rules
- Persistence: in-memory H2 database
- Web layer: static UI with Angular-style interactions
- Security model: sign-in required; approvals restricted to Approver/Admin; destructive actions restricted to Admin
- Testing: Java unit tests and Playwright end-to-end tests are expected for any behavioral change

## Workstreams

### 1. Governance and AI workflow definition
- Confirm repository-level conventions for AI-assisted work
- Establish required output structure for summaries, impacts, tests, and logs
- Define agent responsibilities and role-based collaboration patterns
- Maintain traceability across architecture, implementation, testing, and review

### 2. Planning and spec alignment
- Create OpenSpec planning artifacts for AI workflow governance
- Capture scope, risks, and acceptance criteria without changing runtime code
- Maintain a clear distinction between planning-only artifacts and future implementation work

### 3. Evidence and auditability
- Maintain `docs/ai-interactions.md` as a canonical AI activity log
- Require every significant AI-assisted task to record prompt, response, action, files, and outcome
- Keep review records factual, concise, and easy to audit

### 4. Quality and risk controls
- Define review expectations and technical quality gates
- Ensure role enforcement and workflow integrity are not left to UI-only validation
- Capture open risks and recommended mitigation actions

## Workflow Sequence
1. Analysis hook captures requirement context and expected behavior.
2. Architect Agent validates constraint mapping and design boundaries.
3. Developer Agent prepares implementation plans or tasks for future execution.
4. Test Agent defines validation strategy and negative scenarios.
5. Reviewer Agent checks readiness, risks, and follow-up actions.
6. All artifacts and decision points are logged in `docs/ai-interactions.md`.

## Quality Gates
- Requirements are explicit and mapped to the correct layer.
- Business logic remains outside controllers and configuration.
- Approval rules are enforced server-side and documented.
- Test strategy covers happy path and invalid path scenarios.
- Review findings are captured as actionable follow-up items.
- AI interactions are logged and traceable to artifacts.

## Deliverables
- `AGENTS.md`
- `docs/ai-interactions.md`
- `docs/risk-assessment.md`
- `docs/implementation-plan.md`
- `openspec/changes/<change>/proposal.md`
- `openspec/changes/<change>/tasks.md`
- `.ai/hooks/analysis-hook.md`
- `.ai/hooks/development-hook.md`
- `.ai/hooks/testing-hook.md`
- `.ai/hooks/review-hook.md`

## Validation Strategy
Validation here is documentation-focused and governance-focused rather than application runtime validation.
- Confirm artifact completeness and consistency across agent definitions and hooks
- Verify the log format meets audit requirements
- Check OpenSpec documents reflect planning-only boundaries and no code changes
- Ensure risk analysis covers approval workflow, role enforcement, and AI workflow integrity

## Risks and Mitigations
- Risk: Unclear scope between planning and implementation. Mitigation: Keep the artifact set in planning-only mode and explicitly avoid application logic changes.
- Risk: AI output is not audit-friendly. Mitigation: Require summary, impacted files, tests, and AI interaction log entries for all significant work.
- Risk: Approval workflow rules are treated as UI-only. Mitigation: Document backend enforcement and reviewer quality gates in AGENTS and risk assessment.
- Risk: Missing traceability across SDLC stages. Mitigation: Use agent roles, hooks, and the AI interaction log as evidence artifacts.

## Exit Criteria
The governance and planning artifacts are complete when:
- AGENTS.md defines the operating model for all roles.
- AI interaction log shows reusable audit format and sample entries.
- Implementation plan is explicit and scoped to governance, not runtime code.
- OpenSpec proposal and tasks are generated and aligned to the repository.
- Risk assessment captures key concerns and mitigations.
- Hook definitions enforce workflow outputs and logging requirements.

# Approval Limits Enhancement Implementation Plan

## Purpose
This plan identifies the repository files and workflow points affected by the Approval Limits Enhancement. The change introduces role-based approval thresholds, large-value dual approval, distinct approver enforcement, clear rejection reasons, and non-mutating behavior for rejected approval attempts.

This is a planning document only. No application code is modified as part of this plan.

## Scope
In scope:
- Manager approval cap at 50,000
- Director approval cap at 500,000
- Dual approval for invoices above 500,000
- Second approver must be a Director
- Same-user restriction for both approvers
- Explicit rejection reasons and no state mutation on invalid attempts
- Backend enforcement and frontend alignment with API behavior

Out of scope:
- New invoice creation rules outside this approval policy
- Payment execution or downstream financial posting
- UI redesign or style changes unrelated to approval validation
- Any code changes outside the approval workflow and supporting auth/data contracts

## Impacted Files

| File | Purpose | Why it must change | Complexity | Dependencies |
| --- | --- | --- | --- | --- |
| `backend-java/src/main/java/com/example/invoiceapp/service/ApprovalService.java` | Core approval business logic. | This is the authoritative workflow layer for approve/reject validation. It must enforce Manager/Director limits, high-value dual approval, duplicate approver checks, and the no-mutation rule for rejected attempts. | High | `Invoice`, `InvoiceRepository`, invoice status model, role/session model |
| `backend-java/src/main/java/com/example/invoiceapp/controller/ApprovalsController.java` | REST endpoint for approval and rejection requests. | The API contract must expose clear error states and remain consistent with rejection reasons and role checks. It may need request payload or response shape changes to support dual approval data. | Medium | `ApprovalService`, `UserSession`, `ApiControllerBase`, auth session attributes |
| `backend-java/src/main/java/com/example/invoiceapp/controller/ApiControllerBase.java` | Common API error translation. | Validation failures for approval limits and duplicate approvers should map to explicit client-safe error payloads without altering invoice state. This is the place to keep rejection responses consistent. | Low | `ApprovalService` exceptions, Spring `ResponseEntity` mapping |
| `backend-java/src/main/java/com/example/invoiceapp/model/Invoice.java` | Invoice persistence model. | The invoice may need additional approval metadata (e.g., decision chain, second approver, decision note) to support dual approvals and auditability. Existing fields already capture `decidedBy`, `decidedAt`, and `decisionNote`, but the enhancement may require stronger representation or validation. | Medium | `InvoiceStatus`, persistence layer, repository |
| `backend-java/src/main/java/com/example/invoiceapp/model/InvoiceStatus.java` | Invoice workflow state enumeration. | This file may remain unchanged if the status model already supports pending/approved/rejected; however, it is part of the approval state boundary and must be reviewed for whether any new decision state or explicit error semantics are required. | Low | invoice lifecycle and service logic |
| `backend-java/src/main/java/com/example/invoiceapp/repository/InvoiceRepository.java` | Persistence access for invoice records. | This interface may need custom lookup methods if approval validation requires querying approval history or checking duplicate approver state against stored transaction metadata. | Low | persistence model, service validation logic |
| `backend-java/src/main/java/com/example/invoiceapp/auth/RequireRole.java` | Role annotation contract. | The enhancement introduces Manager and Director roles; this contract must be reviewed to ensure the approved role set is supported and not limited to the current Approver/Admin-only language. | Low | `RequireRoleAspect`, `UserSession`, auth filter |
| `backend-java/src/main/java/com/example/invoiceapp/auth/RequireRoleAspect.java` | Role enforcement at runtime. | Authorization checks must support Manager and Director rules in addition to the existing Approver/Admin gate. This layer is the authoritative source for access enforcement on approval endpoints. | Medium | request/session attributes, `UserSession`, annotations |
| `backend-java/src/main/java/com/example/invoiceapp/auth/SessionAuthFilter.java` | Session-based security filter. | This file is indirectly relevant because the approval decision must come from the authenticated user’s role and username; the filter provides the session context consumed by the approval service. | Low | `SessionStore`, `UserSession` |
| `backend-java/src/main/resources/application.properties` | Demo user configuration. | This is required to define Manager and Director accounts used during validation and QA. Existing entries only include Approver and Admin, so the seed configuration must be updated to reflect the enhancement. | Low | security/auth configuration, demo user setup |
| `backend-java/src/main/java/com/example/invoiceapp/config/DemoUsersProperties.java` | In-memory user registry. | This file defines the set of application users and roles; it must be updated so Manager and Director roles are present and reachable by the approval logic. | Low | `application.properties`, auth/session model |
| `backend-java/src/main/resources/static/app.js` | Frontend approval interaction and error handling. | The UI currently shows a generic approve/reject action for the pending queue. It must align with backend validations, show explicit rejection reasons, and avoid assuming a valid approval request when a role or limit rule would reject it. | Medium | document DOM, `/api/approvals` endpoints, session handling |
| `backend-java/src/main/resources/static/index.html` | Approval screen structure. | The page may require explicit status/error messaging to reflect limit failures, large-invoice dual approval requirements, or user-role restrictions. It is the UI surface that exposes approval logic to the user. | Low | `app.js`, DOM IDs, error region rendering |
| `backend-java/src/test/java/com/example/invoiceapp/service/ApprovalServiceTests.java` | Unit tests for service logic. | This test suite is the primary validation point for new approval rules. It must include success and rejection cases for Manager/Director limits, dual approvals, duplicate approvers, and non-mutating rejection behavior. | Medium | `ApprovalService`, repository mocks, invoice test fixtures |

## Dependency and Workflow Analysis

### 1. Backend validation chain
The approval flow is centered on `ApprovalService`, which currently validates only pending state. The enhancement will extend this authority chain to include:
- user role lookup from the session
- invoice amount comparison against the user role’s maximum
- large-value dual approval requirements
- second approver type and identity validation
- rejection reason generation and no-mutation semantics before any persistence call

### 2. API and UX contract
`ApprovalsController` and `ApiControllerBase` are the API-facing control points. They are essential for translating business-rule failures into consistent client-facing error payloads. The frontend (`app.js` and `index.html`) depends on those payloads to show accurate error messages without silently failing.

### 3. Configuration and role model
The role model is not fully expressed in the current repo. `DemoUsersProperties` and `application.properties` only include Approver and Admin; they must be extended to add the Manager and Director model required by the enhancement.

### 4. Test impact
The existing unit test suite covers core approval semantics but not the role threshold design. The feature will require a second wave of tests for both positive approval paths and invalid business-state scenarios.

## Workstreams

### Workstream 1: Authorization and thresholds
- Map roles to approval authority limits
- Define maximum values for Manager and Director approvals
- Enforce `> 500,000` special-case dual approval rules
- Confirm the second approver must be a Director and must differ from the first approver

### Workstream 2: Backend business logic
- Add service-layer validation before state mutation
- Reject invalid attempts with clear reasons
- Ensure no invoice state changes occur on rejected approval attempts
- Keep role checks and approval decisions in the service layer, not in the controller

### Workstream 3: API and frontend alignment
- Expose explicit rejection reason payloads from the controller layer
- Update static UI to display server-side errors clearly
- Keep UI behavior aligned with backend constraints without duplicate enforcement logic

### Workstream 4: Validation strategy
- Create service tests for valid and invalid Manager approval paths
- Create service tests for valid and invalid Director approval paths
- Add tests for large invoice dual approval rules, including invalid second approver and duplicate approver cases
- Add tests confirming rejected approvals do not alter invoice state

## Implementation Sequence
1. Confirm final role definitions and seeded demo users.
2. Update the approval service to validate authority, dual approval, and same-user restrictions before mutating invoice state.
3. Update controller/error handling to return explicit reason messages for invalid approvals.
4. Update frontend approval UI to reflect the new validation contract and show clear errors.
5. Add unit tests to cover the entire approval matrix and rejection behavior.
6. Run the targeted Java test suite and confirm all threshold and rejection scenarios pass.

## Risks and Mitigations
- Risk: Inconsistent role names or auth model. Mitigation: confirm the exact Manager and Director role strings and seed them before implementation.
- Risk: UI-only validation bypasses backend rules. Mitigation: enforce all logic in service-layer validation and verify with Java tests.
- Risk: Rejected approvals still mutate invoice state. Mitigation: validate before persistence and add regression tests for state immutability.
- Risk: Duplicate approver logic is missed. Mitigation: create explicit tests for same-user re-use and fail the request before mutation.

## Validation Criteria
The enhancement is ready for implementation only when:
- all impacted files above are reviewed and assigned ownership
- the role model and seeded users support Manager and Director
- approval validation is planned for the service layer, not only the UI
- rejection handling is designed to return a clear reason without mutating state
- test scenarios cover both happy paths and failure conditions

## Summary of Direct Impact
The highest-impact files are the approval service and the approval controller, with supporting changes in auth configuration, invoice model constraints, and the static frontend. The approval logic is concentrated in `ApprovalService`, while the user-facing contract and validation messaging are handled by the controller and UI layer.
