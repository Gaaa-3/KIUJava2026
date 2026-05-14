# Assignment 3 Rubric

## Point Value

Assignment 3 is worth:

- `5` regular grading points
- up to `1` assignment-specific bonus point

Course-level bonuses such as early submission and strong first submission are defined separately in `assignment_evaluation_process.md`.

## Internal Mark Model

Technical evaluation first produces a base internal mark:

```text
0-100 internal marks
```

The internal mark is then converted to regular Assignment 3 grading points:

```text
0-5 regular grading points
```

## Internal Mark Categories

### 1. Model Behavior Tests: 30 marks

Excellent:

- Tests cover important `Figure` and `Board` behavior.
- Tests check movement, rotation, boundaries, matching, collapse, scoring, level changes, and game-over behavior.
- Tests use clear inputs and meaningful assertions.

Partial credit:

- Some model tests exist, but important behavior is missing.
- Tests are too broad or unclear.
- Some assertions check implementation details instead of behavior.

Low credit:

- Few or no meaningful model tests.
- Tests mostly check trivial constructors, getters, or "does not throw".

For this rubric, a meaningful test has a behavior-focused assertion. A test with no assertion, or a test whose only assertion is that a constructed object is not null, does not count as meaningful.

### 2. Controller And Event Tests: 20 marks

Excellent:

- Tests cover meaningful `GameController` event behavior.
- Movement, rotation, drop, and level changes are tested.
- Test doubles are used to avoid real applet, screen, time, and delay dependencies.

Partial credit:

- Some controller/event tests exist, but coverage is narrow.
- Tests depend too much on real timing or rendering.
- Tests are fragile or difficult to understand.

Low credit:

- No meaningful controller/event tests.
- Controller behavior is only exercised indirectly without useful assertions.

For controller/event tests, meaningful means the test verifies an event outcome, such as changed figure position, changed color order, changed level, drop behavior, or a recorded fake dependency interaction.

### 3. Test Design Quality: 20 marks

Excellent:

- Tests are deterministic, fast, isolated, and readable.
- Test data is small and focused.
- Test names describe behavior.
- Assertions are specific enough to catch regressions.

Partial credit:

- Tests mostly work, but some are brittle, repetitive, or hard to read.
- Some tests rely on incidental implementation details.
- Some setup is unnecessarily complex.

Low credit:

- Tests are flaky.
- Tests rely on uncontrolled randomness, real sleeping, or manual setup.
- Tests are hard to run or understand.

### 4. Testability Handling: 15 marks

Excellent:

- Small production-code changes, if any, are justified by testability needs.
- Test doubles are simple and appropriate.
- The student avoids broad redesign.
- Behavior is not changed accidentally.

Partial credit:

- Some testability changes are useful, but not all are well justified.
- Some production changes are larger than needed.

Low credit:

- The game is rewritten or heavily refactored.
- Production behavior changes without clear reason.
- Classes or fields are made public only to satisfy tests.

### 5. Test Report: 15 marks

Excellent:

- Report clearly explains what was tested.
- Report identifies the chosen test framework.
- Report explains test doubles and any small production-code changes.
- Report identifies what was hard to test and what design problems were revealed.

Partial credit:

- Report exists but is thin, vague, or misses one of the required topics.

Low credit:

- No meaningful report.
- Report claims coverage without evidence from tests.

## Internal Mark Calculation

Base internal mark:

```text
Model Behavior Tests /30
+ Controller And Event Tests /20
+ Test Design Quality /20
+ Testability Handling /15
+ Test Report /15
= Base internal mark /100
```

## Conversion From Internal Marks To Regular Grading Points

| Base internal mark | Regular grading points |
|---|---:|
| `0-24` | `0/5` |
| `25-49` | `1/5` |
| `50-59` | `2/5` |
| `60-69` | `3/5` |
| `70-84` | `4/5` |
| `85-100` | `5/5` |

## Assignment-Specific Bonus: Coverage Support

Assignment 3 has one optional assignment-specific bonus point.

The bonus is for meaningful test coverage support.

Examples:

- JaCoCo configured and runnable
- Maven or Gradle coverage task configured
- IDE coverage export included with the submission
- another reasonable Java coverage tool used clearly

To receive the bonus, the coverage work must be connected to useful tests.

Coverage numbers alone are not enough. A weak test suite with high line coverage should not receive the bonus.

Bonus conversion:

| Coverage bonus evidence | Assignment-specific bonus |
|---|---:|
| no meaningful coverage support | `0/1` |
| meaningful coverage setup/report connected to useful tests | `1/1` |

## Gating Rules

Apply these rules after assigning the internal mark and converting it to regular grading points.

If multiple gates apply, the most restrictive maximum wins.

### Rule 1: No Meaningful Unit Tests, Maximum `1/5`

If the submission contains no meaningful unit tests, the regular grade cannot exceed `1/5`.

If submitted tests do not compile or cannot be run because of the student's test setup, treat this as no meaningful runnable unit tests for grading purposes.

### Rule 2: No Model Behavior Tests, Maximum `2/5`

If there are no meaningful tests for model behavior such as `Figure` or `Board`, the regular grade cannot exceed `2/5`.

### Rule 3: No Controller/Event Tests, Maximum `4/5`

If model tests exist but there are no meaningful controller or event tests, the regular grade cannot exceed `4/5`.

### Rule 4: Flaky Or Slow Tests, Maximum `4/5`

If tests depend on real sleeps, uncontrolled randomness, or timing-sensitive behavior that makes them flaky or unnecessarily slow, the regular grade cannot exceed `4/5`.

### Rule 5: Missing Test Report, Maximum `4/5`

If no meaningful test report is submitted, the regular grade cannot exceed `4/5`.

## Bonus Eligibility

The assignment-specific coverage bonus can be awarded only if:

- the regular grade is at least `5/5`
- the coverage support is meaningful
- the tests themselves contain useful assertions

## Grading Summary Format

```text
Model Behavior Tests: __/30
Controller And Event Tests: __/20
Test Design Quality: __/20
Testability Handling: __/15
Test Report: __/15
Base internal mark: __/100
Gating rules applied: ...
Regular Assignment 3 grade: __/5
Coverage bonus: __/1
```
