# Assignment 3 Testability Issues

## Purpose

This document describes known testability friction in the refactored Columns game.

These are not defects you must fully fix. They are places where useful tests may need careful design or small testability seams.

## Ignore The Original Version

Do not test or repair:

- `Java2026/src/columns/original`

The original version is not part of Assignment 3.

## Applet Boundary

The top-level `Columns` class is an applet boundary.

It deals with:

- AWT applet lifecycle methods
- keyboard events
- real `Graphics`
- thread startup and stop
- platform timing

This is not the best place to start unit testing.

Prefer testing the model and controller classes under `columns.model`.

## Screen Drawing

Rendering goes through `Screen` and `View`.

Real drawing is hard to test because it depends on AWT `Graphics`.

Use a fake `Screen` if you need to verify drawing-related behavior. A fake screen can record draw calls without opening a real UI.

Most game-rule tests should not need drawing assertions.

## Time And Delay

The controller uses `Platform.currentTime()`, `Platform.delay(long)`, and stored timing state.

Tests should not wait for real time.

Use a fake `Platform` where the test controls the current time and where `delay(long)` does not actually sleep.

## Randomness

Figures are created using `RandomGenerator`.

Tests should avoid uncontrolled randomness.

Use a deterministic `RandomGenerator` that returns a known sequence of values. This makes figure colors predictable.

## Controller Loop

`GameController.runGameLoop` is a long-running game loop.

Testing the full loop directly can be brittle because it combines:

- figure creation
- falling behavior
- event processing
- time checks
- match detection
- collapse
- rendering callbacks
- game-over detection

Prefer focused tests for smaller methods and event behavior.

If you test loop behavior, use fake dependencies that force the loop to stop deterministically.

## Pause Behavior

Pause behavior loops while no key is pressed.

This can hang a test if the fake platform never changes key state.

Only test pause if your fake platform can safely end the loop.

## Mutable Model State

`Board` and `Figure` expose mutable state.

This makes setup easy but also makes tests easy to overfit to implementation details.

Prefer assertions that express game behavior:

- the figure moved left
- a match removed cells
- collapsed cells moved downward
- score increased
- level changed

Avoid unnecessary assertions about every internal field unless the field is the behavior under test.

## Package-Private Classes

Some model classes are package-private.

That is acceptable. Place tests in the same package instead of making everything public.

Example:

```java
package columns.model;
```

## Listener Callbacks

`Board` reports some changes through `ModelListener`.

A fake listener can record:

- level changes
- detected triplets
- field updates
- score updates

This is useful when testing match detection, collapse, scoring, or level changes.

## Small Production-Code Changes

Small changes are allowed if they make tests possible.

Good examples:

- allowing a controller to receive a fake view or platform
- making a helper package-private
- extracting event handling from a loop
- injecting deterministic timing or random behavior

Bad examples:

- rewriting the game
- changing rules to make tests easier
- making many fields public
- replacing the architecture with a new design

## What The Tests Should Reveal

A strong submission should show that the student understands both:

- the game's behavior
- the code's current testability limitations

The report should mention hard-to-test areas honestly. Identifying a design problem is useful; hiding it behind weak tests is not.
