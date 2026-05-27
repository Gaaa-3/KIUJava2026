# Assignment 3 — Test Report

## Framework

JUnit 5 (Jupiter, `org.junit.jupiter:junit-jupiter:5.10.2`).
Build is configured with Maven; coverage is collected with JaCoCo 0.8.12
(`mvn test` → `target/site/jacoco/index.html`).

## What I tested

All tests live in package `columns.model` so they can reach package-private
classes (`Figure`, `Board`, `View`, `GameConfig`) without changing
visibility in production code.

### Model — `FigureTest` (10 tests)
- Constructor seeds the three colour slots from `RandomGenerator.nextInt()`
  via the `Math.abs(v) % 7 + 1` formula, including negative inputs.
- Starting position is top-centre (`WIDTH/2 + 1`, `y = 1`).
- `moveLeft` / `moveRight` / `moveDown` update coordinates by ±1.
- `rotateUp` cycles colours forward (`c[1] ← c[2] ← c[3] ← c[1]`).
- `rotateDown` cycles colours backward.
- Round-trip identities: `rotateUp` then `rotateDown`, and three
  `rotateUp` calls in a row both restore the original colour order.

### Model — `BoardTest` (21 tests)
- `initFields` allocates `[WIDTH+2][DEPTH+2]` arrays.
- `initBoard` resets cells, level, score, and the matched counter.
- `pasteFigure` writes the three colours into `[x][y]`, `[x][y+1]`,
  `[x][y+2]`.
- `dropFigure` falls to the bottom of an empty column, lands on top of a
  stack, and is a no-op when the figure is already near the floor.
- Boundary predicates: `figureMayMoveDown`, `canMoveLeft`, `canMoveRight`
  are checked for true cases, edge-of-board cases, and the
  "blocked by occupied cell" case.
- `isFieldFull` triggers only when row 3 contains a block.
- `findMatches` detects horizontal, vertical, and diagonal triplets,
  clears the matched cells in `oldField`, increments
  `figuresMatchedCounter`, flips `noChanges`, and adds `(level+1)*10` to
  the score. Negative case (no triplet) leaves everything untouched.
- `collapse` packs cleared cells downward, calls `fieldWasUpdated` /
  `scoreUpdated` on the listener, and adds `DScore` to the running score.
- `changeLevelIfNeeded`: below threshold → no change; at threshold →
  level increments by one, counter resets, and `levelHasChanged` fires;
  at `MAX_LEVEL` the level is capped but the listener still fires.

### Controller — `GameControllerTest` (18 tests)
Each event is exercised via the package-private `processEvent` method,
which is the controller's actual dispatch point.

- `LEFT` / `RIGHT` move the figure when allowed, are blocked at the
  edges and by an occupied target cell, and they always clear
  `Platform.isKeyPressed`.
- `UP` / `DOWN` rotate the figure (forward / backward).
- `DROP` snaps the figure to the bottom of its column and resets `tc`
  to 0 so the game-loop timer restarts immediately.
- `LEVEL_UP` / `LEVEL_DOWN` change the level, reset the matched
  counter, and respect the `0 … MAX_LEVEL` bounds.
- `PAUSE` is verified end-to-end with a scripted `isKeyPressed`
  sequence: one loop iteration produces exactly two `delay(500)` calls
  before the next "key press" breaks the loop.
- `NONE` / default leaves figure state untouched.
- The `ModelListener` delegations (`levelHasChanged`, `scoreUpdated`,
  `tripletDetected`, `fieldWasUpdated`) all route to the view and
  produce the expected `Screen` draw calls (asserted against the
  recording `FakeScreen`).
- `foundMatches()` reflects `board.noChanges`.
- `checkTimeAndMoveDownIfNeeded` moves the figure down when the
  elapsed time exceeds the level-dependent threshold, and does nothing
  below the threshold; it also resets `tc` on tick.

`runGameLoop(Platform)` is intentionally **not** invoked directly: it is
an open-ended game loop and its individual responsibilities are already
covered by the targeted controller and board tests above. Exercising it
end-to-end would require a Platform fake that schedules the exact event
sequence to drive the loop to `isFieldFull()`, which adds setup
complexity without finding bugs that the unit tests would miss.

## Test doubles (`columns.model.fakes`)

- **`FakeRandom`** — `RandomGenerator` returning a fixed cycling
  sequence so figure colours and board scenarios are 100% deterministic.
- **`FakeScreen`** — `Screen` that records every draw call as a
  string plus per-method counters. Assertions check that the controller
  actually paints the level, score, and triplet boxes — not just that no
  exception was thrown.
- **`FakePlatform`** — `Platform` with:
  - virtual clock (`now`) advanced by `delay(...)` instead of sleeping;
  - `delay()` records call count and total ms (no real waiting);
  - a queue of `GameEvent`s returned by `getEvent()`;
  - a scriptable `isKeyPressed` sequence used to test the `PAUSE` loop
    without hanging.
- **`RecordingListener`** — `ModelListener` that captures every
  callback so board behaviour can be asserted from the outside.

## What was hard to test

- **`Columns` applet class** — relies on `java.applet.Applet`,
  `java.awt.Event`, and `Thread.stop()`, all removed in modern JDKs.
  It is out of scope per the assignment and the Maven build excludes
  it from compilation.
- **`GameController.runGameLoop`** — the loop only terminates when
  `board.isFieldFull()`, so driving it to completion requires
  hand-crafted event sequences that would essentially re-implement the
  game in the test. The pieces that compose it
  (`processEvent`, `checkTimeAndMoveDownIfNeeded`, `foundMatches`,
  board match/collapse) are all unit-tested individually.
- **`Board.dropFigure` scoring side-effect (`DScore`)** — the formula
  depends on `level`, `y`, and `zz` and is only meaningful after a real
  collapse. We assert the positional outcome (where the figure lands)
  and verify that `collapse` adds `DScore` to `Score`; the exact
  modulo arithmetic is left to the implementation.

## Design problems the tests revealed

- **Mutable public fields on `Board` / `Figure`** (e.g. `newField`,
  `figure`, `Score`, `level`, `x`, `y`, `c`). They make tests easy to
  write but expose the entire model to any caller. A future refactor
  could narrow visibility and introduce intention-revealing methods.
- **`runGameLoop` couples concerns** (timing, input polling, figure
  spawning, match/collapse, game-over check) which is exactly why it
  resists a clean unit test. Splitting it into `tick()` /
  `spawnNewFigure()` / `settle()` would make it trivially testable.
- **`Figure` constructor reads `RandomGenerator`**, but the result is
  derived via `Math.abs(v) % 7 + 1` which is biased and breaks for
  `Integer.MIN_VALUE`. The bias is observable from tests but not the
  game itself; worth a code comment at least.
- **`Platform` is a giant interface** mixing input, timing, screen,
  randomness, and game-loop bookkeeping (`tc`). Splitting it into
  `Clock`, `Input`, `Screen`, and `RandomGenerator` would let the
  controller depend on smaller, focused seams.
- **`View` is hard-coupled to `Screen` coordinates** computed inline
  with `GameConfig` constants. Tests have to assert on draw-call
  contents rather than logical events.

## Production-code changes for testability

**None.** All tests pass without touching production code. Tests are
placed in the same package (`columns.model`) which is the standard Java
pattern for exercising package-private classes; the assignment
explicitly permits this.

## How to run

```bash
# from the repository root, after dropping this Assignment3/ folder next to Java2026/
cd Assignment3
mvn test          # runs 56 JUnit 5 tests, writes JaCoCo HTML report
open target/site/jacoco/index.html
```

## Results

```
56 tests found
56 tests successful
 0 tests failed
```

### JaCoCo coverage (instruction)

| Class            | Coverage |
|------------------|----------|
| `Figure`         | 100%     |
| `Board`          | 100%     |
| `GameEvent`      | 100%     |
| `View`           |  83%     |
| `GameController` |  70% (uncovered = `runGameLoop`, see above) |

The HTML report under `target/site/jacoco/index.html` documents the
exact lines covered.
