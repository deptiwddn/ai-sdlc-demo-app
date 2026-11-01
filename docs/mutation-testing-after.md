## Additional Tests Added

The following test cases were added to target the surviving PIT mutants in the approval workflow and validation guard paths:

- `approveRejectsNullRole`
- `rejectAllowsNullRoleWhenReasonIsProvided`
- `approveAllowsNullApproverForRegularApproval`
- `secondApprovalRejectsNullRoleBeforeApplyingDecisionLogic`
- `rejectRequiresReasonEvenWhenRoleIsNull`

These tests specifically cover:

- Null role handling in approval and rejection flows
- Null approver behavior in a regular approval path
- Validation guards for missing approval role data
- Non-mutating behavior when a validation exception occurs
- Rejection reason enforcement when role information is absent

## Surviving Mutations Addressed

The PIT baseline before improvements was:

- Generated mutations: 60
- Killed: 48
- Survived: 12
- No coverage: 2
- Mutation score: 80%
- Test strength: 83%

After adding the new tests, the result was:

- Generated mutations: 60
- Killed: 49
- Survived: 9
- No coverage: 2
- Mutation score: 82%
- Test strength: 84%

The new tests specifically targeted the residual mutants in the approval guard logic and validation branches, particularly:

- role validation checks
- null role rejection paths
- approval state conservation after validation failures
- fallback logic in reject flows

## Mutation Score After Improvements

- Mutation score after improvements: 82%
- Before: 80%
- Improvement: +2 percentage points

This is a meaningful but still incomplete improvement. The approval workflow logic is stronger than the baseline, but the remaining survivors indicate there are still paths in the service-layer decision logic that are not fully covered by mutation testing.

## Remaining Risks

- Two mutants remain uncovered by PIT (`No coverage: 2`).
- The remaining surviving mutations are concentrated in conditional/decision logic and method-call semantics in `ApprovalService`.
- The approval flow is now better protected for null-role and validation paths, but there are still gaps in broader negative-path coverage for some role and approval-order permutations.
- The current score is acceptable as a baseline improvement, but not yet strong enough for a high-assurance approval workflow in production.

The dominant remaining risk is not that the core approval rules are absent, but that the conditional branches for invalid approvals and role validation are still not fully fortified against mutation-driven defects.

