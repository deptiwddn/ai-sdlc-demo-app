# AI in SDLC Assignment

## 1. Project Overview

This project is an invoice approval demonstration application built with a Spring Boot backend and a frontend that follows Angular-style interaction patterns. The objective of the enhancement was to implement and validate a secure, role-aware approval workflow for invoice decisions while maintaining the project’s architecture and SDLC discipline.

The enhancement focused on approval governance for invoice submissions, with explicit limits by role and staged approval for high-value invoices. The system demonstrates how business requirements can be implemented with a clear separation between controller, service, domain, persistence, auth, and UI concerns while integrating AI-assisted development and review practices into the delivery lifecycle.

## 2. Requirements Implemented

The enhancement implemented the core approval requirements as follows:

- Manager approval limit: Managers may approve invoices up to 50,000.
- Director approval limit: Directors may approve invoices up to 500,000.
- Two-approver workflow: Invoices above 500,000 require a staged second approval by a distinct director.
- Duplicate approver validation: The same user cannot approve the same high-value invoice twice.
- Validation and error handling: Failed approval attempts do not mutate invoice state, and clear validation errors are surfaced to the user and tested at the service layer.

These requirements were implemented as service-layer business rules and validated through both unit tests and end-to-end browser tests.

## 3. Spec-Driven Development

The assignment was executed using a spec-driven, traceable workflow aligned with the repository’s OpenSpec practices.

OpenSpec artifacts were created to establish the requirement context before implementation. This included:

- proposal
- requirements
- acceptance criteria
- tasks
- risks
- test scenarios

These artifacts documented the approval thresholds, workflow behavior, state transitions, and negative scenarios. This ensured that implementation was aligned to a defined specification rather than ad hoc coding. The work remained traceable from requirement to implementation to validation and review.

The generated specification artifacts are located in the repository’s OpenSpec change area and were used to guide implementation and verification.

## 4. AI Agents and Hooks

The project used an agent-based model to separate responsibilities and maintain clear delivery stages.

- Architect Agent: interpreted business and technical requirements, defined workflow boundaries, and mapped the work to the correct application layers.
- Developer Agent: implemented the solution in the service layer and kept business logic out of controllers and configuration classes.
- Tester Agent: defined the unit and end-to-end validation strategy, including negative scenarios and boundary coverage.
- Reviewer Agent: checked correctness, maintainability, architecture alignment, and production risk before final approval.

AI hooks were used during analysis, development, testing, and review to keep the workflow consistent with the project’s SDLC expectations. The hooks provided structure for phase-based execution, ensuring the project remained auditable and aligned with review expectations.

## 5. GitHub Copilot Usage

GitHub Copilot was used across the delivery lifecycle in a disciplined, requirement-driven fashion.

During architecture analysis, Copilot was used to review system boundaries, map business rules to services, and confirm the approval flow responsibilities. During impact analysis, it helped identify the user story requirements and the downstream effects of approval state changes.

During implementation planning, Copilot assisted with requirement decomposition into service-layer tasks, test scenarios, and a maintainable rollout strategy. During feature development, it helped generate the approval logic, role validations, and state transition handling while preserving project conventions.

During testing, Copilot supported unit test design and the Playwright suite for role/amount validation, workflow validation, and no-mutation assertions. In the review phase, it was used to inspect the implementation for maintainability, security, architecture consistency, and risk issues.

Representative examples included approval threshold checks, staged invoice states, duplicate approver prevention, and E2E workflow validation scenarios.

## 6. AI Interaction Logging

AI-assisted activities were tracked in:

- `docs/ai-interactions.md`

The logging approach captured a structured record of each activity, including:

- date and time
- AI tool used
- phase
- prompt provided
- AI response summary
- action taken
- files modified
- outcome

This created an audit trail reflecting how AI support was used in analysis, implementation, testing, and review. The log supports traceability and gives reviewers an evidence trail for the decisions and validations applied during the assignment.

## 7. Implementation Summary

### Approval Limits

The approval service enforces role-based approval limits. Manager approvals are restricted at 50,000, while Director approvals are restricted at 500,000. The logic is enforced in the service layer so the UI cannot be the only enforcement point.

### Two Approver Workflow

Invoices above the Director threshold require a staged approval flow. The first director approval moves the invoice into a `PendingSecondApproval` state. The second approval must come from a different user and must also be a Director. Once validated, the invoice is approved and marked as completed.

### Duplicate Approver Validation

The implementation prevents duplicate user approvals on high-value invoices. A second approval by the same user is rejected, and the invoice remains unchanged. This preserves data integrity and prevents approval gaming or accidental re-entry of the same approver.

Impacted components included:

- backend approval service
- invoice domain model and workflow state
- role validation layer
- controller approval endpoints
- E2E test suite
- unit test suite

## 8. Testing Strategy

### Unit Testing

A dedicated service test suite was created to validate the approval logic and critical guard conditions. Tests covered:

- approval limit checks for Manager and Director
- exact boundary values at 49,999 / 50,000 / 50,001 / 499,999 / 500,000 / 500,001
- negative scenarios such as over-limit approvals and invalid role behavior
- invoice state immutability after failed approval attempts
- high-value invoice staged approval and invalid sequence handling
- duplicate and same-user approval rejection

### Playwright End-to-End Testing

Playwright tests were created to validate the live browser flow for the approval feature. Key scenarios included:

- rejected over-limit approval attempts
- workflow validation for staged approval
- verification of user-visible status transitions
- role-based login and approval flow validation
- UI validation for decision errors and approval states

The E2E suite validated both positive and negative flows so that the feature was exercised as a user would experience it in the application.

## 9. Mutation Testing

### Before Improvements

Mutation Score: 80%

Key Findings:
- Mutation testing was configured and executed against the service-layer business logic.
- The initial baseline showed surviving mutants in approval validation and decision paths.
- Coverage was improved by adding tests for null validation and state-preservation paths.

### After Improvements

Mutation Score: 82%

Additional Tests Added:
- null-role validation checks
- null-approver guard coverage
- rejection reason validation coverage
- state preservation assertions after failed validation

Outcome:
Mutation score improved from 80% to 82%.

## 10. Pull Request Review Findings

The PR review was completed as a production-focused maintainer review. The review documented findings by severity and status, covering business logic correctness, validation, security assumptions, maintainability, and testing coverage.

The findings were captured in `docs/pr-review-findings.md` and classified as:

- fixed findings
- deferred findings
- dismissed findings

The review emphasized that the feature was largely correct and aligned with the business requirements, but a few production-oriented concerns remained around authorization matrix completeness and stronger domain validation.

## 11. Challenges Faced

The project presented several real-world engineering challenges:

- requirement interpretation around approval thresholds and actor responsibilities
- workflow design for staged approval for high-value invoices
- testing challenges involving state transitions and no-mutation requirements
- runtime configuration drift between source-level role assumptions and live demo authentication
- mutation testing setup and the need to focus analysis on business logic without broadening scope

These issues were addressed by keeping the implementation focused on the service layer, validating the runtime configuration, and maintaining traceability from requirements to tests.

## 12. Lessons Learned

Several lessons were reinforced throughout the assignment:

- spec-driven development improves clarity and traceability
- AI-assisted development works best when paired with explicit requirements and review gates
- test-first and mutation-based thinking increase confidence in critical business logic
- review processes are essential for production-grade workflow validation
- service-level enforcement is mandatory for approval integrity and data safety

## 13. Conclusion

The AI-SDLC assignment was completed successfully with the implementation of the approval enhancement in a controlled, traceable, and reviewable manner. The project demonstrates a strong integration of business requirements, security-aware workflow design, AI-assisted development, and quality validation using both automated tests and mutation testing.

The final result reflects a professional engineering workflow that is aligned with SDLC best practices, supports auditability, and provides a foundation for continued improvement through stronger automation and deeper validation.

## Video Demo

- Demo video: https://nagarro-my.sharepoint.com/:v:/p/deepak_tiwari01/IQCL6EvVyOU-Ra8y5PoCfbXMAaxTAoDlQ2dBGJ4kNBdEPpQ?e=UxFJqs
- Pull request: https://github.com/deptiwddn/ai-sdlc-demo-app/pull/1

