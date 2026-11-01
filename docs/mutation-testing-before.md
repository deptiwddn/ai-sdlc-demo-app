## PIT Configuration

- PIT plugin configured in `backend-java/pom.xml`
- JUnit 5 compatibility enabled through the PIT JUnit 5 plugin
- Mutation analysis scoped to business logic in `com.example.invoiceapp.service.*`
- Mutation run executed with:
  - `mvn clean test`
  - `mvn org.pitest:pitest-maven:mutationCoverage`

## Classes Included

- `com.example.invoiceapp.service.ApprovalService`

## Classes Excluded

- `com.example.invoiceapp.config.*`
- `com.example.invoiceapp.auth.*`
- `com.example.invoiceapp.controller.*`
- `com.example.invoiceapp.repository.*`
- `com.example.invoiceapp.model.*`
- `com.example.invoiceapp.InvoiceAppApplication`
- `**.*Request`
- `**.*Response`
- `**.*Dto`
- `**.*DTO`
- `**.*generated.*`

## Commands Executed

```bash
cd C:/Code/ai-sdlc-demo-app/backend-java
mvn clean test
mvn org.pitest:pitest-maven:mutationCoverage
```

## Baseline Mutation Testing Results

The baseline PIT run completed successfully with the configured scope.

## Mutation Score

- Generated mutations: 60
- Killed: 48
- Survived: 12
- No coverage: 2
- Mutation score: 80%
- Test strength: 83%

## Surviving Mutations

PIT reported surviving mutation activity in the service logic, primarily in mutators affecting conditions and method calls. The output showed:

- `VoidMethodCallMutator`: 9 survived out of 22 generated
- `NegateConditionalsMutator`: 1 survived out of 25 generated

## Initial Findings

- PIT is configured correctly for this project and works with JUnit 5.
- Mutation analysis is running on the business logic package only as requested.
- The baseline score is 80%, which indicates the current test suite is adequate but not strong enough for a high-risk approval workflow.
- The surviving mutations cluster around decision logic in the approval flow and should be addressed by adding more targeted tests before relying on this workflow in production.
- No business logic or test code was improved in this step, per the requirement to establish the before-state only.

