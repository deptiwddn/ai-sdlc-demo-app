# OpenSpec Test Scenarios: Approval Limits Enhancement

## Happy Paths

### TS-1: Manager approves within limit
- Given a Manager user and an invoice valued at 50,000
- When the user approves the invoice
- Then the approval is accepted

### TS-2: Director approves within limit
- Given a Director user and an invoice valued at 500,000
- When the user approves the invoice
- Then the approval is accepted

### TS-3: High-value invoice with valid dual approval
- Given an invoice valued above 500,000
- And the first approver is a valid user
- And the second approver is a distinct Director
- When the approval sequence is submitted
- Then the approval is accepted

## Negative Scenarios

### TS-4: Manager exceeds authority
- Given a Manager user and an invoice valued above 50,000
- When the approval is attempted
- Then the system rejects the request with a reason stating the value exceeds Manager authority
- And invoice state remains unchanged

### TS-5: Director exceeds single approver authority
- Given a Director user and an invoice valued above 500,000 without a second approver
- When the approval is attempted
- Then the system rejects the request with a reason stating dual approval is required
- And invoice state remains unchanged

### TS-6: Second approver is not a Director
- Given an invoice above 500,000
- And the second approver is a Manager or non-Director user
- When approval is submitted
- Then the system rejects the request with a reason stating the second approver must be a Director
- And invoice state remains unchanged

### TS-7: Same user used twice
- Given an invoice above 500,000
- And both approver slots are assigned to the same user
- When approval is submitted
- Then the system rejects the request with a reason stating approvers must be distinct
- And invoice state remains unchanged

### TS-8: Rejection reason is explicit
- Given any invalid approval attempt
- When the request fails validation
- Then the API or service response contains a clear reason describing the rule violation

### TS-9: Failed approval does not mutate invoice state
- Given a valid invoice in a pending state
- And an invalid approval workflow is attempted
- When the request is rejected
- Then the invoice status, approval count, and related state remain unchanged

