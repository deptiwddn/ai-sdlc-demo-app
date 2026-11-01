# OpenSpec Risks: Approval Limits Enhancement

## Risk 1: Ambiguous role mapping
Without a clear mapping between business roles and authenticated users, a Manager or Director approval may be assigned to the wrong user type. This can weaken enforcement and allow improper approvals.

Mitigation:
- Define and validate the role-to-authorization mapping before implementation.
- Add tests covering role-based approval authority and invalid role combinations.

## Risk 2: Incomplete validation in high-value workflows
A large invoice workflow may be partially validated in the UI but not in the backend, allowing invalid requests to reach service logic or persist changes.

Mitigation:
- Enforce all validation rules in the service layer.
- Require backend tests for invalid transitions and rejection handling.

## Risk 3: Duplicate approver bypass
If the approval logic does not explicitly check identity uniqueness, the same user may be allowed to satisfy both approver roles.

Mitigation:
- Add dedicated tests confirming the same user cannot serve as both approvers.
- Fail approval attempts before any state mutation occurs.

## Risk 4: State mutation on rejected approvals
If validation errors are not handled before persistence, rejected approval attempts could alter invoice state or approval records even when the business rule fails.

Mitigation:
- Ensure validation occurs before any state-changing call.
- Add regression tests to confirm state remains unchanged after rejection.

## Risk 5: Insufficient rejection clarity
If rejection reasons are vague or generic, support teams and auditors may not understand which policy rule prevented the approval.

Mitigation:
- Standardize rejection payloads to include specific business reason codes or messages.
- Document expected reason strings in the acceptance criteria and test scenarios.

## Residual Risk
Some implementation details may depend on the final auth model and existing controller contract. These should be confirmed during detailed implementation and validation to prevent drift between the workflow rules and runtime behavior.

