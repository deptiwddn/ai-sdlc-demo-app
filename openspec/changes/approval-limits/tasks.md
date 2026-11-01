# OpenSpec Tasks: Approval Limits Enhancement

## Objective
Implement the approval threshold and multi-approver validation model required for Manager and Director authority levels while preserving invoice integrity and explicit rejection behavior.

## Tasks

### 1. Confirm workflow and role model
- Review the existing invoice approval workflow and identify the authoritative approval service boundary.
- Confirm the role names and authorization model for Manager and Director users.
- Map the invoice value thresholds to the project’s approval state model and service conventions.

### 2. Define approval limits and rule matrix
- Document the value limits for Manager and Director approvals.
- Define the high-value rule for invoices above 500,000.
- Define the requirement that the second approver must be a Director and that the approvers must be distinct.

### 3. Implement service-layer validation
- Add value and role validation in the approval service before mutating invoice state.
- Prevent approvals that exceed the user’s authority threshold.
- Enforce dual approval and same-user checks before approving large invoices.

### 4. Implement rejection handling
- Return explicit rejection reasons for invalid approval attempts.
- Ensure rejected attempts do not change the invoice state or approval records.
- Maintain a consistent validation and error contract for consumers of the approval API.

### 5. Add validation tests
- Add tests for Manager approval at 50,000 and at limits above 50,000.
- Add tests for Director approval at 500,000 and above 500,000.
- Add tests for large invoice dual approval success and rejection conditions.
- Add tests for duplicate approver and non-director second approver cases.
- Add tests confirming state is unchanged after rejected approval attempts.

### 6. Review for architecture and risk alignment
- Confirm rules remain in service-layer logic, not controllers or UI-only code.
- Validate that rejection reasons and invoice state immutability are preserved.
- Check for gaps in role-based enforcement and failure-path coverage.

## Acceptance Criteria
- Approval thresholds and role restrictions are documented in the change artifacts.
- Service-layer validation enforces the approval model for all invoice approvals.
- Rejected attempts provide clear reasons and do not mutate invoice state.
- Unit and integration tests cover valid and invalid approval scenarios for Manager and Director roles.

