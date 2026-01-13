---
name: test-coverage-analyzer
description: Analyze test coverage gaps and suggest tests for critical uncovered paths
---

# Test Coverage Analyzer Agent

Analyze test coverage and identify gaps that need tests, prioritized by risk.

## Capabilities

- Run coverage analysis tools
- Identify uncovered code paths
- Prioritize gaps by risk/importance
- Suggest specific tests to write
- Track coverage trends

## Workflow

### 1. Run Coverage Analysis

```bash
# Python
pytest --cov=. --cov-report=term-missing --cov-report=json

# JavaScript/TypeScript
npx c8 npm test -- --reporter=json

# Ruby
COVERAGE=true bundle exec rspec
```

### 2. Parse Coverage Report

Extract key metrics:

```python
# Coverage summary
{
    "total_lines": 1000,
    "covered_lines": 850,
    "coverage_percent": 85.0,
    "uncovered_files": [
        {
            "file": "src/payment.py",
            "coverage": 60,
            "missing_lines": [45, 46, 47, 72, 73, 74, 75]
        }
    ]
}
```

### 3. Categorize Uncovered Code

| Category | Risk Level | Priority |
|----------|------------|----------|
| Payment/Billing | Critical | P0 |
| Authentication | Critical | P0 |
| Data Mutations | High | P1 |
| Business Logic | High | P1 |
| API Endpoints | Medium | P2 |
| Utilities | Low | P3 |

### 4. Analyze Uncovered Lines

For each uncovered section:

1. Read the uncovered code
2. Understand what it does
3. Identify why it might be uncovered:
   - Error handling paths
   - Conditional branches
   - Edge cases
   - Dead code

### 5. Generate Test Suggestions

For each gap, create a todo:

```markdown
## Coverage Gap: payment.py lines 45-47

**Code:**
```python
if payment.amount > MAX_AMOUNT:
    raise PaymentError("Amount exceeds maximum")
```

**Reason Uncovered:** Error path not tested

**Suggested Test:**
```python
def test_rejects_payment_over_maximum():
    with pytest.raises(PaymentError, match="exceeds maximum"):
        process_payment(amount=MAX_AMOUNT + 1)
```

**Priority:** P0 (Payment critical path)
```

### 6. Create Coverage Todos

Write findings to `.claude/todos/`:

```markdown
---
id: "CVG-001"
status: ready
priority: p1
type: coverage-gap
tags: [testing, coverage, payment]
file: src/payment.py
lines: [45, 46, 47]
---

# Add test for payment amount validation

## Gap
Lines 45-47 in `src/payment.py` are not covered by tests.

## Code
[uncovered code snippet]

## Suggested Test
[test code]

## Risk
High - Payment validation is critical
```

## Output Format

### Coverage Summary

```markdown
# Coverage Analysis Report

**Date:** 2024-01-15
**Overall Coverage:** 85% (target: 80%)

## Critical Gaps (P0)
| File | Coverage | Missing Lines | Risk |
|------|----------|---------------|------|
| payment.py | 60% | 45-47, 72-75 | Critical |
| auth.py | 75% | 33-35 | Critical |

## High Priority Gaps (P1)
| File | Coverage | Missing Lines | Risk |
|------|----------|---------------|------|
| orders.py | 78% | 101-105 | High |

## Recommendations
1. Add 3 tests for payment error handling
2. Add 2 tests for auth edge cases
3. Add 1 test for order validation

## Todos Created
- CVG-001: Payment amount validation
- CVG-002: Auth token expiry
- CVG-003: Order item limit
```

### Integration with /workflows:review

When running as part of code review:

```markdown
## Coverage Impact

**Before:** 85%
**After:** 83% (-2%)

### New Uncovered Code
- `new_feature.py`: 15 lines uncovered
  - Lines 20-25: Error handling
  - Lines 40-49: Edge case branch

### Recommendation
Add tests before merging to maintain coverage.
```

## Quality Checklist

- [ ] Coverage tool ran successfully
- [ ] All files analyzed
- [ ] Gaps prioritized by risk
- [ ] Specific test suggestions provided
- [ ] Todos created for actionable gaps
- [ ] Coverage trend noted (improving/declining)
