---
name: generate-tests
description: Generate comprehensive tests for specified files or features following project conventions
argument-hint: "[file path or feature description]"
allowed-tools: Skill(test-patterns)
---

# Generate Tests Command

Generate comprehensive tests for new or existing code.

## Usage

```
/generate-tests [target]
```

**Arguments:**
- `target` - File path, directory, class name, or feature description

## Examples

```bash
# Generate tests for a specific file
/generate-tests src/services/user-service.ts

# Generate tests for a feature
/generate-tests "user authentication flow"

# Generate tests for a class
/generate-tests PaymentProcessor

# Generate tests for a directory
/generate-tests src/utils/
```

## Workflow

### 1. Analysis Phase

First, analyze the target code:

```markdown
## Test Target Analysis

**Target:** [file/feature identified]
**Language:** [detected language]
**Framework:** [detected test framework]

### Code Structure
- Functions/Methods: [list]
- Dependencies: [list external deps]
- Side Effects: [I/O, network, database]

### Existing Tests
- [ ] Check if tests already exist
- [ ] Identify coverage gaps
```

### 2. Choose Testing Approach

Use AskUserQuestion to determine testing strategy:

```yaml
questions:
  - question: "Which testing approach matches your goals?"
    header: "Test Type"
    multiSelect: false
    options:
      - label: "Unit tests (Recommended)"
        description: "Fast, isolated tests for individual functions. Best for TDD and quick feedback loops."
      - label: "Integration tests"
        description: "Test component interactions and data flow. More realistic but slower than unit tests."
      - label: "End-to-end tests"
        description: "Full user workflows from UI to database. Most realistic but slowest and most brittle."
      - label: "Balanced pyramid"
        description: "Mix of all three: many unit, some integration, few e2e. Industry best practice."
```

**Based on answer:**
- **Unit tests:** Generate isolated tests with mocks/stubs
- **Integration tests:** Generate tests with real dependencies
- **End-to-end tests:** Generate full workflow tests
- **Balanced pyramid:** Generate 70% unit, 20% integration, 10% e2e

**Default:** If no answer provided, use "Unit tests" (recommended option)

### 3. Test Strategy

Determine the testing approach based on user selection:

```markdown
## Testing Strategy

### Unit Tests Needed
| Function | Test Cases | Priority |
|----------|------------|----------|
| `function1` | happy path, edge cases, errors | High |
| `function2` | validation, transformation | Medium |

### Integration Tests (if applicable)
- [ ] API endpoint tests
- [ ] Database interaction tests
- [ ] External service mocks

### Test Data Requirements
- Factory patterns needed
- Fixtures to create
- Mock data structures
```

### 4. Generate Tests

Use the `test-generator` agent:

```markdown
Invoke the test-generator agent to create tests:
- Follow project conventions (discover from existing tests)
- Use appropriate assertions for the language
- Include edge cases and error scenarios
- Add meaningful test descriptions
```

### 4. Verification

```bash
# Run generated tests
[test command for project]

# Check coverage
[coverage command for project]
```

## Test Generation Principles

### Follow AAA Pattern
```
Arrange - Set up test data and preconditions
Act     - Execute the code under test
Assert  - Verify the expected outcome
```

### Test Independence
- Each test should run in isolation
- No shared mutable state between tests
- Tests should not depend on execution order

### Meaningful Names
```
✓ test_create_user_with_valid_email_returns_user_object
✓ test_create_user_with_invalid_email_raises_validation_error
✗ test_create_user_1
✗ test_user_stuff
```

### Coverage Targets
- **Critical paths:** 100% coverage
- **Business logic:** 90%+ coverage
- **Utilities:** 80%+ coverage
- **UI components:** Focus on behavior, not implementation

## Output

Tests are created following project structure:

```
tests/
├── unit/
│   └── [matching source structure]
├── integration/
│   └── [API/service tests]
└── fixtures/
    └── [test data factories]
```

## Related

- `/analyze-coverage` - Identify coverage gaps
- `/generate-api-tests` - Generate API-specific tests
- `test-patterns` skill - Test pattern reference
