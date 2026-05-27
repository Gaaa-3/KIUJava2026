# Assignment 3 — Unit tests for refactored Columns

## Layout
```
Assignment3/
├── REPORT.md           # required test report (see for full details)
├── README.md           # this file
├── pom.xml             # Maven build + JUnit 5 + JaCoCo
└── test/
    └── columns/
        └── model/
            ├── FigureTest.java          (10 tests)
            ├── BoardTest.java           (21 tests)
            ├── GameControllerTest.java  (18 tests, incl. PAUSE loop)
            └── fakes/
                ├── FakePlatform.java
                ├── FakeScreen.java
                ├── FakeRandom.java
                └── RecordingListener.java
```

## Run
Drop this `Assignment3/` folder at the repository root, next to `Java2026/`,
then:
```bash
cd Assignment3
mvn test
# JaCoCo HTML report:  target/site/jacoco/index.html
```

`pom.xml` points `sourceDirectory` at `../Java2026/src` so no source files
are duplicated. The legacy applet (`columns/Columns.java` and
`columns/original/**`) is excluded from compilation because it uses APIs
removed in modern JDKs and is out of scope per the assignment.

## Result
`56/56 tests pass`.  Coverage: `Figure` 100%, `Board` 100%,
`GameEvent` 100%, `View` 83%, `GameController` 70% (uncovered =
`runGameLoop`, which the report explains).

## Production-code changes
**None.** Tests sit in the same package as the production classes
(`columns.model`), the standard Java pattern for testing package-private
code — explicitly allowed by the assignment.
