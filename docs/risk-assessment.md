# Risk Assessment

## Overview
This risk assessment covers the AI-assisted governance and workflow artifacts for the invoice approval demo project. The focus is on workflow integrity, auditability, and role enforcement rather than application runtime implementation.

## Risk Register

### 1. Approval workflow integrity risk
- Risk: Approval and rejection logic could be enforced only in the UI, leaving backend validation inconsistent.
- Impact: Business rules may be bypassed or poorly enforced.
- Likelihood: Medium
- Severity: High
- Mitigation: Require backend validation, document role checks, and require reviewer validation before merge.

### 2. Agent role ambiguity risk
- Risk: Different AI agents may overlap in scope or make conflicting implementation assumptions.
- Impact: Inconsistent outputs and reduced traceability.
- Likelihood: Medium
- Severity: Medium
- Mitigation: Define explicit responsibilities, inputs, outputs, quality gates, and when-to-use guidance in `AGENTS.md`.

### 3. Documentation drift risk
- Risk: Planning, AI interaction records, and implementation status may diverge over time.
- Impact: Audit evidence becomes difficult to trust.
- Likelihood: Medium
- Severity: Medium
- Mitigation: Require logging of prompt, response, action, files, and outcomes for all major AI-assisted activities.

### 4. Scope creep into application code
- Risk: Planning artifacts might be confused with implementation tasks and trigger runtime changes.
- Impact: Unapproved code changes and reduced control over SDLC boundaries.
- Likelihood: Low
- Severity: High
- Mitigation: Explicitly keep planning artifacts in governance-only scope and require implementation to be handled in a separate apply workflow.

### 5. Test coverage gaps risk
- Risk: Critical invoice approval states may not be exercised across valid and invalid transitions.
- Impact: Edge-case defects remain undetected.
- Likelihood: Medium
- Severity: High
- Mitigation: Require test-first behavior and negative path analysis in the Test Agent and reviewer checks.

### 6. Review process weakness risk
- Risk: Review feedback may remain vague or not tied to project conventions.
- Impact: Quality issues may pass without remediation.
- Likelihood: Medium
- Severity: Medium
- Mitigation: Require structured review output with findings, risks, and follow-up actions.

## Controls
- Architecture review gates before implementation.
- Service-layer invariants documented in AGENTS and project conventions.
- AI interaction log as continuous evidence record.
- Hook definitions requiring stage-specific outputs and logging.
- Reviewer quality gates for security, workflow integrity, and regression risk.

## Residual Risk
Residual risk remains moderate because AI-assisted work can still introduce ambiguity if prompts are underspecified. This is reduced by requiring explicit requirements, acceptance criteria, and review sign-off.

## Follow-Up Actions
- Continue maintaining `docs/ai-interactions.md` for each major AI-assisted effort.
- Review whether additional approval-state edge cases need explicit specification.
- Keep OpenSpec artifacts aligned with implementation changes when the repository moves from planning to execution.
- Reassess risk during major feature additions or workflow changes.

