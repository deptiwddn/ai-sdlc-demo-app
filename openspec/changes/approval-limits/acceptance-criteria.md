# OpenSpec Acceptance Criteria: Approval Limits Enhancement

1. A Manager user can approve an invoice valued at 50,000 without triggering a rejection.
2. A Manager user cannot approve an invoice valued above 50,000 and must receive a clear rejection reason.
3. A Director user can approve an invoice valued at 500,000 without triggering a rejection.
4. A Director user cannot approve an invoice valued above 500,000 as a single approver and must receive a clear rejection reason.
5. An invoice above 500,000 requires two distinct approvers before approval is accepted.
6. The second approver for a high-value invoice must be a Director.
7. An approval attempt with the same user as both approvers is rejected.
8. Any rejected approval attempt returns an explanation that identifies the violated rule, such as value limit, role restriction, or duplicate approver.
9. When an approval attempt is rejected, the invoice remains in its original state and no approval record is persisted or mutated.
10. The approval rules are enforced in backend service logic and are not dependent on a front-end-only validation path.
11. The implementation includes tests covering valid approval paths and invalid approval attempts for each rule above.

