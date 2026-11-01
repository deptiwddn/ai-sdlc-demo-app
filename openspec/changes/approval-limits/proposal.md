# OpenSpec Proposal: Approval Limits Enhancement

## Summary
This change introduces role-based approval thresholds and dual-approval enforcement for the invoice approval workflow. It defines how Manager and Director approvals are authorized, under which conditions a second approver is required, and how failed approval attempts are handled without changing invoice state.

## Problem Statement
The current approval workflow does not distinguish approval authority by invoice value. Without explicit value limits and validation rules, users may approve invoices beyond their authority, or a large invoice may be approved by a single user without required governance controls.

## Goal
Define and document the approval authorization model for invoice approvals so that the system enforces safe, role-aware limits and does not allow invalid approval attempts to alter invoice state.

## Scope
In scope:
- Manager approval authority up to 50,000
- Director approval authority up to 500,000
- Dual approval requirement above 500,000
- Second approver requirement and director-only constraint
- Same-user prevention for multi-approver workflows
- Rejection reason and non-mutating behavior for rejected approval attempts

Out of scope:
- New invoice creation logic
- Payment execution or posting workflows
- UI styling or dashboard redesign
- Any unrelated workflow changes outside approval authorization and validation

## Requirements
1. Manager users may approve invoices with a value up to 50,000.
2. Director users may approve invoices with a value up to 500,000.
3. Invoices above 500,000 require two distinct approvers.
4. The second approver must be a director.
5. The same user may never count as both approvers.
6. Rejected approval attempts must return a clear reason.
7. Rejected approval attempts must not modify invoice state.

## Assumptions
- Manager and Director are distinct authorized roles in the invoice workflow.
- The approval authorization model is enforced in service-layer business logic rather than by UI-only checks.
- Existing invoice data and approval state transitions remain the authoritative source for validation.

## Success Criteria
- Approval rules are explicitly documented and testable.
- Large-value invoices require dual, distinct approvers with a director as the second approver.
- Invalid approval requests are rejected with explicit reasons and unchanged invoice state.
- The requirement set is ready for implementation and validation in service and workflow tests.

