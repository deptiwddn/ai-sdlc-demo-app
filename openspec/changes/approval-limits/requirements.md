# OpenSpec Requirements: Approval Limits Enhancement

## Functional Requirements

### R1. Manager approval cap
The system must allow a Manager user to approve an invoice only when the invoice value is less than or equal to 50,000.

### R2. Director approval cap
The system must allow a Director user to approve an invoice only when the invoice value is less than or equal to 500,000.

### R3. High-value dual approval requirement
If an invoice value exceeds 500,000, the system must require two distinct approvers before the invoice can be approved.

### R4. Director as second approver
When dual approval is required, the second approver must be a Director. A second approver who is not a Director must be rejected.

### R5. Distinct approvers only
The system must reject any approval attempt where the same user is used as both approvers in the same approval chain.

### R6. Clear rejection reasons
The system must return a clear and explicit rejection reason whenever an approval attempt is invalid under the authorization or limit rules.

### R7. No mutation on rejection
If an approval attempt is rejected, the invoice state and approval history must remain unchanged.

## Non-Functional Requirements

### N1. Service-layer enforcement
Approval limit checks and role validation must be enforced in backend service logic, not only in client-side UI code.

### N2. Deterministic validation
The same invoice and same approver combination must always produce the same validation result.

### N3. Auditability
Approval rejections must provide enough context to explain why a request was denied and which rule was violated.

## Out of Scope
- Remapping or redesigning the invoice lifecycle outside approval authorization
- Policy changes outside Manager/Director approval thresholds
- UI-only validation without server-side enforcement

