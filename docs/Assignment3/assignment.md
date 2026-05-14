# Assignment 3: Unit Testing The Columns Game

## Context

You are given a refactored Java version of a Columns-style falling-block game.

The code is in:

- `Java2026/src/columns`

There is also an older legacy version in:

- `Java2026/src/columns/original`

Ignore `columns/original` completely. Assignment 3 is only about testing the refactored version.

## Main Goal

Create a useful set of unit tests for the refactored Columns game.

Your tests should verify real game behavior. The goal is not to execute as many lines as possible with weak assertions. The goal is to make important behavior safer to change.

## What You Need To Test

Your tests should cover both model behavior and controller event behavior.

Expected model test areas:

- `Figure` movement
- `Figure` rotation
- board movement boundaries
- match detection
- field collapse and packing
- scoring behavior
- level changes
- game-over detection

Expected controller/event test areas:

- left movement event
- right movement event
- rotate up event
- rotate down event
- drop event
- level up event
- level down event

Optional controller/event test area:

- pause behavior, if you can test it with suitable test doubles

You do not need to test the applet itself or real drawing on screen.

## Test Framework

Use any reasonable Java unit testing framework.

Examples:

- JUnit 4
- JUnit 5
- TestNG

Choose one framework and use it consistently.

## Testing Package-Private Code

You may test package-private model classes directly.

For example, you may place tests in the same package as the production class:

- `columns.model`
- `columns.model.kernel`

Do not make classes, methods, or fields public only because a test needs access.

## Production-Code Changes

Small production-code changes are allowed only when needed for testability.

Acceptable examples:

- adding constructor injection
- adding small test seams
- making randomness deterministic in tests
- making time or delay behavior controllable in tests
- adjusting package-private visibility where reasonable
- extracting a tiny helper if it makes a behavior testable

Do not turn this into a refactoring assignment.

Do not rewrite the game, redesign the architecture, or perform unrelated cleanup.

## Test Doubles

You may use fake or stub implementations for boundaries such as:

- `Platform`
- `Screen`
- `RandomGenerator`
- model listener callbacks

Your tests should be deterministic and fast. They should not depend on real time delays, real random values, or real screen drawing.

Hand-written test doubles are enough. Mocking libraries such as Mockito are also acceptable if you use them clearly and consistently.

## Required Report

Submit a short test report.

The report should explain:

- which behavior you tested
- which test framework you used
- which test doubles you used
- what was hard to test
- what design problems the tests revealed
- any small production-code changes you made for testability

Keep the report concise and evidence-based.

## Bonus Track: Coverage Support

Test coverage support is optional bonus work.

You may earn bonus credit by configuring and reporting coverage with a reasonable Java coverage tool, such as:

- JaCoCo
- Maven coverage plugin
- Gradle coverage plugin
- IDE coverage export
- another reasonable Java coverage tool

Coverage numbers alone are not enough. A coverage report supports good tests; it does not replace meaningful assertions.

## Deliverables

Submit:

- unit test source code
- any small production-code changes needed for testability
- a short test report
- optional coverage configuration or coverage report for the bonus track

Submit your work through the same repository submission process used for the course assignments unless the instructor gives a different instruction.

## Constraints

- Do not test `columns/original`.
- Do not rewrite the game.
- Do not make broad refactoring changes.
- Do not add tests whose only purpose is calling a getter or no-argument constructor without asserting meaningful behavior.
- Do not depend on uncontrolled randomness.
- Do not depend on real waiting or sleeping in unit tests.
- Do not require a real applet screen for unit tests.

## Suggested Workflow

1. Read the refactored Columns code in `Java2026/src/columns`.
2. Identify the model behavior that can already be tested directly.
3. Add tests for `Figure` and `Board` behavior first.
4. Add simple test doubles for platform, screen, randomness, or listener boundaries.
5. Add controller event tests.
6. Make only small production-code changes if a behavior cannot be tested otherwise.
7. Run all tests.
8. Write the short test report.

## Important

Weak tests are not useful.

A test with no meaningful assertion, a test that only calls a method without checking behavior, or a test that depends on random timing does not demonstrate real verification.
