package columns.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import columns.model.fakes.FakePlatform;
import columns.model.fakes.FakeRandom;

class GameControllerTest {

	FakePlatform platform;
	GameController controller;

	@BeforeEach
	void setUp() {
		platform = new FakePlatform(new FakeRandom(0, 1, 2));
		controller = new GameController(platform);
		controller.board.initBoard();
		controller.board.figure = new Figure(platform.random);
		controller.board.figure.x = 4;
		controller.board.figure.y = 5;
	}

	// ---------- movement events ----------

	@Test
	void leftEventMovesFigureLeftWhenPossible() {
		int x0 = controller.board.figure.x;
		controller.processEvent(GameEvent.LEFT);
		assertEquals(x0 - 1, controller.board.figure.x);
		assertFalse(platform.isKeyPressed(), "processEvent must consume the key");
	}

	@Test
	void leftEventBlockedAtLeftEdge() {
		controller.board.figure.x = 1;
		controller.processEvent(GameEvent.LEFT);
		assertEquals(1, controller.board.figure.x);
	}

	@Test
	void rightEventMovesFigureRightWhenPossible() {
		int x0 = controller.board.figure.x;
		controller.processEvent(GameEvent.RIGHT);
		assertEquals(x0 + 1, controller.board.figure.x);
	}

	@Test
	void rightEventBlockedAtRightEdge() {
		controller.board.figure.x = GameConfig.WIDTH;
		controller.processEvent(GameEvent.RIGHT);
		assertEquals(GameConfig.WIDTH, controller.board.figure.x);
	}

	@Test
	void rightEventBlockedByOccupiedCell() {
		controller.board.figure.x = 4;
		controller.board.figure.y = 5;
		controller.board.newField[5][7] = 1;
		controller.processEvent(GameEvent.RIGHT);
		assertEquals(4, controller.board.figure.x);
	}

	// ---------- rotation events ----------

	@Test
	void rotateUpEventCyclesColorsForward() {
		controller.board.figure = new Figure(new FakeRandom(0, 1, 2)); // {0,1,2,3}
		controller.processEvent(GameEvent.UP);
		assertArrayEquals(new int[] { 0, 2, 3, 1 }, controller.board.figure.c);
	}

	@Test
	void rotateDownEventCyclesColorsBackward() {
		controller.board.figure = new Figure(new FakeRandom(0, 1, 2));
		controller.processEvent(GameEvent.DOWN);
		assertArrayEquals(new int[] { 0, 3, 1, 2 }, controller.board.figure.c);
	}

	// ---------- drop event ----------

	@Test
	void dropEventSendsFigureToBottomAndResetsTc() {
		controller.board.figure.x = 4;
		controller.board.figure.y = 1;
		platform.tc = 123;
		controller.processEvent(GameEvent.DROP);
		assertEquals(GameConfig.DEPTH - 2, controller.board.figure.y);
		assertEquals(0L, platform.getTc(), "DROP must reset tc to 0");
	}

	// ---------- level events ----------

	@Test
	void levelUpEventIncrementsLevelAndResetsCounter() {
		controller.board.level = 2;
		controller.board.figuresMatchedCounter = 10;
		controller.processEvent(GameEvent.LEVEL_UP);
		assertEquals(3, controller.board.level);
		assertEquals(0, controller.board.figuresMatchedCounter);
	}

	@Test
	void levelUpEventDoesNotExceedMax() {
		controller.board.level = GameConfig.MAX_LEVEL;
		controller.processEvent(GameEvent.LEVEL_UP);
		assertEquals(GameConfig.MAX_LEVEL, controller.board.level);
	}

	@Test
	void levelDownEventDecrementsLevelAndResetsCounter() {
		controller.board.level = 3;
		controller.board.figuresMatchedCounter = 10;
		controller.processEvent(GameEvent.LEVEL_DOWN);
		assertEquals(2, controller.board.level);
		assertEquals(0, controller.board.figuresMatchedCounter);
	}

	@Test
	void levelDownEventDoesNotGoBelowZero() {
		controller.board.level = 0;
		controller.processEvent(GameEvent.LEVEL_DOWN);
		assertEquals(0, controller.board.level);
	}

	// ---------- pause event ----------

	@Test
	void pauseEventLoopsUntilNextKeyPressAndDelays() {
		// First two isKeyPressed() polls return false (one loop iteration),
		// third returns true to break the loop.
		platform.scriptKeyPressed(false, true);
		controller.processEvent(GameEvent.PAUSE);
		// one loop iteration -> two delay(500) calls
		assertEquals(2, platform.delayCalls);
		assertEquals(1000, platform.totalDelay);
	}

	// ---------- ModelListener delegation ----------

	@Test
	void levelHasChangedDelegatesToViewAndUpdatesScreen() {
		platform.screen.calls.clear();
		controller.levelHasChanged(4);
		assertTrue(platform.screen.drawStringCount > 0, "view.showLevel must draw level text");
		assertTrue(platform.screen.calls.stream().anyMatch(s -> s.contains("Level: 4")));
	}

	@Test
	void scoreUpdatedDelegatesToViewAndDrawsScore() {
		platform.screen.calls.clear();
		controller.scoreUpdated(77L);
		assertTrue(platform.screen.calls.stream().anyMatch(s -> s.contains("Score: 77")));
	}

	@Test
	void tripletDetectedDrawsThreeWhiteBoxes() {
		platform.screen.calls.clear();
		controller.tripletDetected(1, 2, 3, 4, 5, 6);
		// each drawBox(c=8) emits 2 drawRect + 1 fillRect; we expect 3 boxes -> >= 3 drawRect calls
		assertTrue(platform.screen.drawRectCount >= 3,
				"expected drawRect calls for 3 triplet boxes, got " + platform.screen.drawRectCount);
	}

	@Test
	void fieldWasUpdatedDrawsEntireField() {
		controller.board.newField[2][3] = 4;
		platform.screen.calls.clear();
		controller.fieldWasUpdated(controller.board.newField);
		// drawField iterates WIDTH*DEPTH cells; each cell triggers at least one draw call.
		assertTrue(platform.screen.calls.size() >= GameConfig.WIDTH * GameConfig.DEPTH);
	}

	// ---------- foundMatches helper ----------

	@Test
	void foundMatchesReflectsNoChangesFlag() {
		controller.board.noChanges = true;
		assertFalse(controller.foundMatches());
		controller.board.noChanges = false;
		assertTrue(controller.foundMatches());
	}

	// ---------- checkTimeAndMoveDownIfNeeded ----------

	@Test
	void checkTimeAndMoveDownMovesFigureWhenTimeExceeded() {
		controller.board.level = 0;
		controller.board.figure.y = 5;
		platform.tc = 0;
		// threshold: (MAX_LEVEL - 0) * TIME_SHIFT + MIN_TIME_SHIFT = 7*250+200 = 1950
		platform.now = 5_000;
		controller.checkTimeAndMoveDownIfNeeded(platform);
		assertEquals(6, controller.board.figure.y, "figure must drop one row when time exceeded");
		assertEquals(5_000, platform.getTc(), "tc must be reset to currentTime");
	}

	@Test
	void checkTimeAndMoveDownDoesNothingBelowThreshold() {
		controller.board.level = 0;
		controller.board.figure.y = 5;
		platform.tc = 0;
		platform.now = 100; // far below 1950
		controller.checkTimeAndMoveDownIfNeeded(platform);
		assertEquals(5, controller.board.figure.y);
	}

	// ---------- noneEvent / default ----------

	@Test
	void noneEventLeavesFigureUntouched() {
		int x0 = controller.board.figure.x;
		int y0 = controller.board.figure.y;
		int[] c0 = controller.board.figure.c.clone();
		controller.processEvent(GameEvent.NONE);
		assertEquals(x0, controller.board.figure.x);
		assertEquals(y0, controller.board.figure.y);
		assertArrayEquals(c0, controller.board.figure.c);
	}
}
