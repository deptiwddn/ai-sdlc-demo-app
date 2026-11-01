# Analysis Hook

## Trigger
Triggered when a requirement, feature, workflow question, or architecture concern is being explored before implementation begins.

Examples:
- requirement clarification
- workflow review
- architecture assessment
- risk identification
- acceptance criteria framing

## Required output
The output must include:
- a concise summary of the requirement or issue
- key constraints and assumptions
- architecture or layer impact
- expected behavior and acceptance criteria
- potential risks or open questions
- a note on whether the work is planning-only or implementation-ready

## Logging requirements
Record the activity in `docs/ai-interactions.md` using the standard template:
- Date/Time
- AI Tool Used
- Phase: Analysis
- Prompt Provided
- AI Response Summary
- Action Taken
- Files Modified
- Outcome

If no files are changed, record `Files Modified: None (analysis-only)`.

