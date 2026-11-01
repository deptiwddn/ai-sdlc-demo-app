# Development Hook

## Trigger
Triggered when implementation planning or coding work is about to begin for a feature, fix, or workflow update.

Examples:
- implementing a business rule
- modifying an approval flow
- updating controller, service, or repository logic
- preparing an OpenSpec apply task for later execution

## Required output
The output must include:
- summary of the implementation scope
- impacted files or components
- design or architecture notes
- validation expectations
- known assumptions or follow-up items
- confirmation that business logic remains in the correct layer

## Logging requirements
Record the activity in `docs/ai-interactions.md` under the Development phase with:
- Date/Time
- AI Tool Used
- Phase: Development
- Prompt Provided
- AI Response Summary
- Action Taken
- Files Modified
- Outcome

If the work is still planning-only, clearly state that no runtime code changes were made.

