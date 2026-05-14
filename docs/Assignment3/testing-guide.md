# Assignment 3 Testing Guide

## Purpose

This guide gives practical advice for testing the refactored Columns game.

The assignment is about useful unit tests. Your tests should verify real game behavior and be reliable enough to catch regressions.

## Start With Deterministic Model Tests

Start with classes whose behavior can be checked without the applet or real screen:

- `Figure`
- `Board`
- `GameConfig`
- simple `GameEvent` behavior where relevant

Good first tests:

- rotating a figure up changes colors in the expected order
- rotating a figure down changes colors in the expected order
- moving left decreases `x`
- moving right increases `x`
- moving down increases `y`
- a board prevents moving left at the left border
- a board prevents moving right at the right border
- a board detects a full field when row `3` contains blocks; in this model, row `3` is the game-over check row because a new vertical figure occupies rows `1-3`

## Use Same-Package Tests When Needed

Some classes are package-private. That is acceptable.

Put test classes in the same package instead of making production code public:

```java
package columns.model;
```

This keeps the production API smaller while still allowing focused tests.

## Control Randomness

`Figure` receives a `RandomGenerator`.

Use a deterministic fake random generator in tests. For example, make it return a known sequence so the figure colors are predictable.

Avoid tests that depend on Java's real `Random`.

## Avoid Real Time

Controller behavior uses time and delays through `Platform`.

Do not write tests that wait for real time to pass. Instead, use a fake `Platform` where:

- `currentTime()` returns values you control
- `delay(long)` records the requested delay or does nothing
- key state and events are controlled by the test

Fast tests are easier to trust.

## Avoid Real Screen Drawing

Do not test by opening a real applet or inspecting actual pixels.

Use a fake `Screen` that records calls such as:

- `setColor`
- `fillRect`
- `drawRect`
- `drawString`
- `clearRect`

Then assert the important calls happened.

For most model tests, you should not need a screen at all.

## Suggested Board Tests

Useful `Board` tests include:

- `initBoard` clears the field, score, level, and match counter
- `pasteFigure` writes the figure colors into the expected cells
- `dropFigure` moves a figure to the lowest available valid position
- `canMoveLeft` is false at the left edge
- `canMoveLeft` is false when a blocking cell exists
- `canMoveRight` is false at the right edge
- `canMoveRight` is false when a blocking cell exists
- `figureMayMoveDown` is false at the bottom
- `figureMayMoveDown` is false when the cell below the figure is occupied
- `findMatches` detects vertical matches
- `findMatches` detects horizontal matches
- `findMatches` detects diagonal matches
- `collapse` packs remaining cells downward
- score increases after a detected match
- level increases after the configured match threshold
- `isFieldFull` detects blocks in row `3`

You do not need to test every possible grid arrangement. Prefer a focused set of examples that proves the important rules.

## Suggested Controller Tests

Controller tests should use fake dependencies.

Useful event tests include:

- `LEFT` moves the current figure left when the board allows it
- `LEFT` does not move the figure through a wall or block
- `RIGHT` moves the current figure right when allowed
- `UP` rotates the figure upward
- `DOWN` rotates the figure downward
- `DROP` moves the figure to its drop position and resets controller timing through the platform
- `LEVEL_UP` increases level only up to the maximum
- `LEVEL_DOWN` decreases level only down to zero
- manual level changes reset the match counter

Pause behavior is harder because it loops while no key is pressed. If you test it, use a fake `Platform` that can end the loop deterministically.

## Good Assertions

Good tests assert observable results:

- figure position changed
- figure colors changed in the expected order
- board cells contain expected values
- score changed by the expected amount
- level changed or did not change
- fake listener received the expected callback
- fake screen recorded expected draw calls
- fake platform recorded timing changes

Weak tests only call a method and check that no exception was thrown.

A meaningful test has a behavior-focused assertion. A test with no assertion, or a test whose only assertion is that a constructed object is not null, is not meaningful for this assignment.

## Small Testability Changes

If behavior is hard to test, make the smallest production-code change that creates a test seam.

Reasonable changes:

- inject a dependency instead of constructing it inside the method
- expose behavior through package-private methods instead of public API
- split a long method only enough to test a rule directly
- replace hard-coded time or randomness with an injectable interface

Hand-written fakes and stubs are sufficient. Mocking libraries such as Mockito are allowed, but they should not make the test harder to understand than a simple fake would.

Avoid changes that alter game behavior.

## What To Avoid

- tests for `columns/original`
- tests that require a real applet
- tests that sleep
- tests that depend on real random values
- tests that only improve coverage numbers
- tests that assert implementation details unrelated to behavior
- broad refactoring disguised as testing

## Report Notes

Your report should connect your tests to behavior.

For example:

- "I used a deterministic `RandomGenerator` so figure colors were predictable."
- "I used a fake `Platform` to test controller events without sleeping."
- "The controller was harder to test than `Board` because event handling, drawing, and timing are still coupled."

Do not write a long essay. Focus on evidence.
