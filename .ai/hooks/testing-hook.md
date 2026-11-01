# Testing Hook

## Trigger
Triggered when validation strategy, unit tests, integration tests, or end-to-end scenarios are being defined or executed.

Examples:
- test-first planning
- approval workflow validation
- invalid transition checks
- role-based access verification
- regression analysis

## Required output
The output must include:
- test objectives and scenarios
- happy-path and negative-path coverage
- role-based authorization checks
- invalid transition or missing state handling
- validation status and gaps
- any follow-up test recommendations

## Logging requirements
Record the activity in `docs/ai-interactions.md` under the Testing phase with:
- Date/Time
- AI Tool Used
- Phase: Testing
- Prompt Provided
- AI Response Summary
- Action Taken
- Files Modified
- Outcome

If the work is planning only, include `Files Modified: None (planning-only)` and explicitly note that no application code was modified.

