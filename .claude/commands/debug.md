---
name: debug
description: Analyze errors, stack traces, and logs to identify root causes and recommend fixes
argument-hint: "[optional: error description or paste error logs]"
allowed-tools: Skill(error-analysis)
---

# Debug Command

Systematically analyze errors, stack traces, and logs to identify root causes and implement fixes.

## Usage

```bash
# Analyze inline error
/debug "TypeError: Cannot read property 'id' of undefined"

# Analyze with stack trace
/debug "
Error: Connection refused
  at Database.connect (src/db/connection.ts:23)
  at async initialize (src/server.ts:45)
"

# Analyze log file
/debug logs/application.log

# Interactive mode (will prompt for details)
/debug
```

## Progress Tracking

Use TodoWrite to track the 5-phase error analysis workflow:

```
TodoWrite:
1. Parse error information and extract key details - pending
2. Categorize error type and severity - pending
3. Perform root cause analysis - pending
4. Generate fix recommendations (hotfix + long-term) - pending
5. Create action items and todos - pending
```

Update status: `pending` → `in_progress` → `completed` as you progress.

## Workflow

### Phase 1: Gather Error Information

**If arguments provided ($ARGUMENTS):**
- Parse error message, stack trace, logs from input
- Extract key details automatically

**If no arguments:**

Use AskUserQuestion to gather error context:

```yaml
questions:
  - question: "How frequently does this error occur?"
    header: "Frequency"
    multiSelect: false
    options:
      - label: "One-time occurrence"
        description: "Error happened once, cannot reproduce. May be transient issue."
      - label: "Intermittent (rare)"
        description: "Happens occasionally, hard to predict. Likely race condition or edge case."
      - label: "Recurring (daily)"
        description: "Happens regularly under specific conditions. Consistent pattern exists."
      - label: "Constant (blocking)"
        description: "Happens every time, blocking users. Critical production issue."

  - question: "What environment is affected?"
    header: "Environment"
    multiSelect: true
    options:
      - label: "Production"
        description: "Affecting live users"
      - label: "Staging"
        description: "Caught in pre-production"
      - label: "Development"
        description: "Only seen locally"
```

**Then prompt for additional details:**
- Error message
- Stack trace (if available)
- Error logs (paste or file path)
- When it occurred
- What user was doing
- Recent changes (deploy, config change, etc.)

**Analysis depth based on frequency:**
- **One-time:** Quick analysis, suggest monitoring
- **Intermittent:** Deep RCA with hypothesis testing for race conditions
- **Recurring:** Pattern analysis, identify common factors
- **Constant:** Immediate hotfix priority, thorough RCA afterward

**Default:** If no answer provided, assume "Recurring (daily)"

**For log files:**
```bash
# Read last 100 lines of log file
tail -100 $ARGUMENTS
```

**For pasted error messages:**
- Accept multi-line error messages
- Extract stack traces automatically
- Identify error type from message

### Phase 2: Launch Error Analyst

Invoke the error-analyst agent with full context using Task syntax:

```
Task error-analyst: "Analyze this error and provide comprehensive root cause analysis:

## Error Details
[Error message and stack trace]

## Context
- Environment: [env]
- Time: [timestamp]
- Frequency: [occurrences]
- Recent changes: [changes]

## Required Analysis

Please provide:

1. **Error Classification**
   - Type (runtime, compile, infrastructure, etc.)
   - Category (null reference, connection, timeout, etc.)
   - Severity (critical, high, medium, low)

2. **Root Cause Analysis**
   - Hypotheses (what could cause this)
   - Evidence for each hypothesis
   - Confirmed root cause
   - Timeline/sequence of events

3. **Immediate Fix (Hotfix)**
   - Specific code changes needed
   - File paths and line numbers
   - Code snippets showing before/after

4. **Long-term Fixes**
   - Preventive measures
   - Refactoring recommendations
   - Tests to add
   - Monitoring improvements

5. **Action Items**
   - Immediate actions (today)
   - Short-term actions (this sprint)
   - Long-term actions (next sprint)
   - Priority for each item
"
```

**Wait for agent to complete analysis** before proceeding to next phase.

### Phase 3: Present Analysis Report

Format the error-analyst output as a structured report:

```markdown
# Error Analysis Report

## Summary
[Brief description of error and impact]

**Error Type:** [Type]
**Severity:** 🔴 Critical / 🟡 High / 🔵 Medium / ⚪ Low
**Status:** Root cause identified

---

## Error Details

**Message:** [Error message]
**Location:** [file:line]
**Stack Trace:**
```
[Stack trace]
```

**Context:**
- Environment: [env]
- Time: [timestamp]
- Frequency: [occurrences]

---

## Root Cause Analysis

### Timeline
- [HH:MM] - [Event]
- [HH:MM] - [Event]

### Hypotheses Evaluated
1. **[Hypothesis 1]**
   - Evidence: [Evidence]
   - Result: [Confirmed/Rejected]

2. **[Hypothesis 2]**
   - Evidence: [Evidence]
   - Result: [Confirmed/Rejected]

### Confirmed Root Cause
[Detailed explanation of root cause]

---

## Fix Recommendations

### Immediate Fix (Hotfix)

**File:** `[file_path:line]`

```[language]
// Before
[old code]

// After
[new code]
```

**Steps:**
1. [Step]
2. [Step]
3. Deploy and verify

### Long-term Fixes

1. **[Fix 1 Title]**
   - Description: [Description]
   - Effort: Small/Medium/Large
   - Priority: High/Medium/Low

2. **[Fix 2 Title]**
   - Description: [Description]
   - Effort: Small/Medium/Large
   - Priority: High/Medium/Low

---

## Action Items

### Immediate (Today)
- [ ] [Action]
- [ ] [Action]

### Short-term (This Sprint)
- [ ] [Action]
- [ ] [Action]

### Long-term (Next Sprint)
- [ ] [Action]
- [ ] [Action]

---

## Prevention

To prevent similar errors:
- [Prevention measure]
- [Prevention measure]
```

### Phase 4: Auto-Create Todos for Fixes

After presenting the analysis report, automatically create todo files in `.claude/todos/` for all action items:

```markdown
## Creating Todos

Automatically creating todos for all action items in `.claude/todos/`...

For each action item:
- **Immediate actions** → Create as `{id}-ready-p1-{description}.md` (Priority 1, ready to work)
- **Short-term actions** → Create as `{id}-ready-p2-{description}.md` (Priority 2)
- **Long-term actions** → Create as `{id}-pending-p3-{description}.md` (Priority 3, needs planning)
```

**Todo file format:** (use file-todos skill patterns)

```yaml
---
status: ready     # or pending for long-term items
priority: p1      # p1 (immediate), p2 (short-term), p3 (long-term)
issue_id: "042"
tags: [bug-fix, error-handling]
dependencies: []
---

# [Action Item Title]

## Problem Statement
[From error analysis - the root cause]

## Proposed Solution
[From fix recommendations]

## Technical Details
- **Affected Files**: [Files from analysis]
- **Effort**: [Small/Medium/Large]
- **Risk**: [Low/Medium/High]

## Implementation Steps
1. [Step from fix recommendations]
2. [Step]
3. [Step]

## Acceptance Criteria
- [ ] Fix implemented as specified
- [ ] Unit tests added to prevent regression
- [ ] Integration tests pass
- [ ] Verified in staging environment
- [ ] Deployed to production
- [ ] Monitoring shows error resolved

## Work Log

### [Date] - Created from Debug Session
**Source:** `/debug` command analysis
**Root Cause:** [Brief root cause]
**Priority Rationale:** [Why this priority]
```

**Determine next issue ID:**
```bash
# Find highest issue ID in .claude/todos/
ls .claude/todos/ 2>/dev/null | grep -E '^[0-9]+' | sed 's/-.*$//' | sort -n | tail -1

# Increment by 1 for each new todo
```

**Example todos created:**
```markdown
✅ Created todos:
- `042-ready-p1-fix-null-check-user-service.md` (Immediate fix)
- `043-ready-p2-add-profile-validation.md` (Short-term improvement)
- `044-pending-p3-migrate-legacy-user-profiles.md` (Long-term refactoring)

Run `/resolve_todo_parallel` to start working on these todos.
```

### Phase 5: Summary

Present final summary:

```markdown
## Debug Session Complete

**Error:** [Brief error description]
**Root Cause:** [Root cause in one sentence]
**Fix Status:** Recommendations provided and todos created

---

### Next Steps

1. **Immediate:**
   - Apply hotfix: [file:line]
   - Test fix locally
   - Deploy to staging

2. **Verification:**
   - Monitor error rates
   - Verify fix in [environment]
   - Check logs for related errors

3. **Long-term:**
   - Schedule short-term fixes (this sprint)
   - Plan long-term refactorings (next sprint)
   - Update monitoring/alerts

---

### Todos Created

**Total:** [X] todos created in `.claude/todos/`

**By Priority:**
- P1 (Immediate): [count] todos
- P2 (Short-term): [count] todos
- P3 (Long-term): [count] todos

**Next Commands:**
```bash
# Work on todos in parallel
/resolve_todo_parallel

# Or triage todos if priorities need adjustment
/triage

# Or view all ready todos
ls .claude/todos/*-ready-*.md
```

---

### Resources

- Error analysis performed by `error-analyst` agent
- Root cause analysis using `error-analysis` skill
- Debugging methodology from `debugging-workflow` skill
- Todo creation using `file-todos` skill patterns
```

## Integration

This command integrates with:

### Agents
- **error-analyst** - Performs error analysis and RCA (automatically invoked)

### Skills
- **error-analysis** - Error patterns and RCA techniques (bound via `allowed-tools`)
- **debugging-workflow** - Systematic debugging methodology (auto-discovered)
- **file-todos** - Todo file format and patterns (referenced for todo creation)

### Commands
- **/triage** - Triage action items if prioritization needs adjustment
- **/resolve_todo_parallel** - Work on multiple generated todos efficiently
- **/health-report** - Track error metrics over time
- **/reproduce-bug** - Reproduce bugs from GitHub issues (complementary workflow)

## Examples

### Example 1: TypeScript Null Reference

**Input:**
```bash
/debug "TypeError: Cannot read property 'id' of undefined at UserService.getUser (src/services/user.ts:45)"
```

**Output:**
```markdown
# Error Analysis Report

## Summary
Null reference error when accessing user profile ID. Affects 5% of requests (legacy users).

**Error Type:** Runtime Error - Null Reference
**Severity:** 🟡 High
**Status:** Root cause identified

## Root Cause
Code assumes all users have profiles, but legacy users (created before 2023) don't have profile records.

## Fix Recommendations

### Immediate Fix (Hotfix)

**File:** `src/services/user.ts:45`

```typescript
// Before
const userId = user.profile.id;

// After
const userId = user.profile?.id ?? user.id;
```

### Long-term Fixes
1. Add profile validation at user creation
2. Migrate legacy users to have profiles
3. Add regression tests for users without profiles

## Todos Created
- `042-ready-p1-fix-null-check-user-service.md`
- `043-ready-p2-add-profile-validation.md`
- `044-pending-p3-migrate-legacy-user-profiles.md`
```

### Example 2: Connection Error

**Input:**
```bash
/debug "Error: ECONNREFUSED 127.0.0.1:5432"
```

**Output:**
```markdown
# Error Analysis Report

## Summary
Database connection refused. Service cannot connect to PostgreSQL.

**Error Type:** Infrastructure - Connection Refused
**Severity:** 🔴 Critical
**Status:** Root cause identified

## Root Cause
PostgreSQL service is not running. Application attempting to connect to port 5432 but no listener present.

## Fix Recommendations

### Immediate Fix (Hotfix)

**Steps:**
1. Start PostgreSQL service:
   ```bash
   sudo systemctl start postgresql
   # or
   brew services start postgresql
   ```
2. Verify connection:
   ```bash
   psql -h localhost -p 5432 -U postgres
   ```

### Long-term Fixes
1. Add database health checks on app startup
2. Implement connection retry logic with exponential backoff
3. Add monitoring alerts for database downtime
4. Document database setup in README

## Todos Created
- `045-ready-p1-start-postgresql-service.md`
- `046-ready-p2-add-db-health-checks.md`
- `047-pending-p3-implement-connection-retry.md`
```

### Example 3: Log File Analysis

**Input:**
```bash
/debug logs/application-2024-01-10.log
```

**Output:**
```markdown
# Error Analysis Report

## Summary
Multiple errors detected in log file. Primary issue: Memory exhaustion leading to crashes.

**Error Type:** Infrastructure - Resource Exhaustion
**Severity:** 🔴 Critical
**Status:** Root cause identified

## Error Pattern Analysis

Analyzed last 100 lines of `logs/application-2024-01-10.log`:

**Errors found:**
- 15 occurrences: "FATAL ERROR: Allocation failed - JavaScript heap out of memory"
- 8 occurrences: "Error: Cannot allocate memory"
- Timing: All between 14:00-15:00 UTC

**Log correlation:**
```
14:23:45 INFO  Processing batch job (1000 items)
14:23:50 WARN  Memory usage: 85%
14:24:00 ERROR Cannot allocate memory
14:24:01 FATAL JavaScript heap out of memory
14:24:02 INFO  Process crashed, restarting...
```

## Root Cause
Batch job processing 1000 items simultaneously without pagination. Memory usage spikes when loading all items into memory at once.

## Fix Recommendations

### Immediate Fix (Hotfix)

**File:** `src/jobs/batch-processor.ts:67`

```typescript
// Before
const items = await fetchAllItems(); // Loads all 1000 items
await processItems(items);

// After
const BATCH_SIZE = 100;
for (let i = 0; i < totalCount; i += BATCH_SIZE) {
  const items = await fetchItems(i, BATCH_SIZE);
  await processItems(items);
}
```

### Long-term Fixes
1. Implement streaming processing for large datasets
2. Add memory monitoring and alerts
3. Configure Node.js heap size limits
4. Add queue-based processing with workers

## Todos Created
- `048-ready-p1-add-pagination-batch-processor.md`
- `049-ready-p2-add-memory-monitoring.md`
- `050-pending-p3-implement-streaming-processor.md`
```

## Related Commands

- `/reproduce-bug [issue]` - Reproduce bugs from GitHub issues
- `/triage` - Triage findings and create todos
- `/health-report` - Generate codebase health metrics
- `/report-bug` - Report bugs in the compound-engineering plugin
- `/scan-debt` - Scan codebase for technical debt
- `/resolve_todo_parallel` - Work on multiple todos efficiently

## Notes

- The `error-analysis` skill is automatically available via `allowed-tools`
- The `debugging-workflow` skill is auto-discovered when debugging
- Todos follow the `file-todos` skill format
- All action items are automatically converted to todos (no user prompt)
- Plain text logs and stack traces are supported (JSON logs: future enhancement)
