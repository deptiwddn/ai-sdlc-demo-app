# Review Hook

## Trigger
Triggered before merge, release, or final sign-off on a change or governance artifact.

Examples:
- PR review
- architecture review
- quality gate validation
- audit evidence review
- risk assessment for a planned or implemented change

## Required output
The output must include:
- review findings and recommendations
- architecture and security considerations
- validation status or evidence reviewed
- risks and follow-up actions
- recommendation on readiness or outstanding gaps

## Logging requirements
Record the activity in `docs/ai-interactions.md` under the Review phase with:
- Date/Time
- AI Tool Used
- Phase: Review
- Prompt Provided
- AI Response Summary
- Action Taken
- Files Modified
- Outcome

When the review is documentation-only or planning-only, record that fact in the `Files Modified` and `Outcome` fields.

