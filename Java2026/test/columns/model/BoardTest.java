package columns.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import columns.model.fakes.FakeRandom;
import columns.model.fakes.RecordingListener;

class BoardTest {

	Board board;
	RecordingListener listener;

	@BeforeEach
	void setUp() {
		board = new Board();
		listener = new RecordingListener();
		board.setModelListener(listener);
		board.initFields();
		board.initBoard();
	}

	// ---------- initialisation ----------

	@Test
	void initBoardZeroesAllCellsAndStats() {
		board.newField[3][3] = 5;
		board.oldField[3][3] = 5;
		board.level = 4;
		board.Score = 999;
		board.figuresMatchedCounter = 12;

		board.initBoard();

		for (int c = 0; c < GameConfig.WIDTH + 1; c++) {
			for (int r = 0; r < GameConfig.DEPTH + 1; r++) {
				assertEquals(0, board.newField[c][r]);
				assertEquals(0, board.oldField[c][r]);
			}
		}
		assertEquals(0, board.level);
		assertEquals(0L, board.Score);
		assertEquals(0, board.figuresMatchedCounter);
	}

	@Test
	void initFieldsAllocatesArraysWithBorder() {
		Board b = new Board();
		b.initFields();
		assertEquals(GameConfig.WIDTH + 2, b.newField.length);
		assertEquals(GameConfig.DEPTH + 2, b.newField[0].length);
		assertEquals(GameConfig.WIDTH + 2, b.oldField.length);
		assertEquals(GameConfig.DEPTH + 2, b.oldField[0].length);
	}

	// ---------- pasteFigure ----------

	@Test
	void pasteFigureWritesThreeStackedCells() {
		Figure f = new Figure(new FakeRandom(0, 1, 2)); // c = {0,1,2,3}
		f.x = 3;
		f.y = 5;
		board.pasteFigure(f);
		assertEquals(1, board.newField[3][5]);
		assertEquals(2, board.newField[3][6]);
		assertEquals(3, board.newField[3][7]);
	}

	// ---------- dropFigure ----------

	@Test
	void dropFigureFallsToBottomOfEmptyColumn() {
		Figure f = new Figure(new FakeRandom(0));
		f.x = 4;
		f.y = 1;
		board.level = 0;
		board.dropFigure(f);
		assertEquals(GameConfig.DEPTH - 2, f.y, "should land so the bottom cell sits at DEPTH");
	}

	@Test
	void dropFigureStopsOnTopOfStack() {
		Figure f = new Figure(new FakeRandom(0));
		f.x = 4;
		f.y = 1;
		// Pre-fill bottom 3 rows of column 4.
		board.newField[4][GameConfig.DEPTH] = 5;
		board.newField[4][GameConfig.DEPTH - 1] = 5;
		board.newField[4][GameConfig.DEPTH - 2] = 5;
		board.dropFigure(f);
		// zz scans down until it finds a 0 — stops at DEPTH-3; new y = zz - 2.
		assertEquals(GameConfig.DEPTH - 3 - 2, f.y);
	}

	@Test
	void dropFigureDoesNothingIfFigureAlreadyNearBottom() {
		Figure f = new Figure(new FakeRandom(0));
		f.x = 4;
		f.y = GameConfig.DEPTH - 2; // guard: y < DEPTH-2 is false
		board.dropFigure(f);
		assertEquals(GameConfig.DEPTH - 2, f.y);
	}

	// ---------- movement boundaries ----------

	@Test
	void figureMayMoveDownTrueWhenSpaceBelow() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 3;
		board.figure.y = 1;
		assertTrue(board.figureMayMoveDown());
	}

	@Test
	void figureMayMoveDownFalseAtBottomDepth() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 3;
		board.figure.y = GameConfig.DEPTH - 2;
		assertFalse(board.figureMayMoveDown());
	}

	@Test
	void figureMayMoveDownFalseWhenBlockedByCellBelow() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 3;
		board.figure.y = 5;
		board.newField[3][8] = 1; // y+3
		assertFalse(board.figureMayMoveDown());
	}

	@Test
	void canMoveLeftFalseAtLeftEdge() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 1;
		board.figure.y = 5;
		assertFalse(board.canMoveLeft());
	}

	@Test
	void canMoveLeftFalseWhenLeftCellOccupied() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 4;
		board.figure.y = 5;
		board.newField[3][7] = 1; // x-1, y+2
		assertFalse(board.canMoveLeft());
	}

	@Test
	void canMoveLeftTrueOtherwise() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 4;
		board.figure.y = 5;
		assertTrue(board.canMoveLeft());
	}

	@Test
	void canMoveRightFalseAtRightEdge() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = GameConfig.WIDTH;
		board.figure.y = 5;
		assertFalse(board.canMoveRight());
	}

	@Test
	void canMoveRightFalseWhenRightCellOccupied() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 4;
		board.figure.y = 5;
		board.newField[5][7] = 1; // x+1, y+2
		assertFalse(board.canMoveRight());
	}

	@Test
	void canMoveRightTrueOtherwise() {
		board.figure = new Figure(new FakeRandom(0));
		board.figure.x = 4;
		board.figure.y = 5;
		assertTrue(board.canMoveRight());
	}

	// ---------- game over / field full ----------

	@Test
	void isFieldFullDetectsAnyBlockInRowThree() {
		assertFalse(board.isFieldFull());
		board.newField[2][3] = 4;
		assertTrue(board.isFieldFull());
	}

	@Test
	void isFieldFullFalseWhenOnlyDeeperRowsHaveBlocks() {
		board.newField[2][5] = 4;
		board.newField[3][10] = 4;
		assertFalse(board.isFieldFull());
	}

	// ---------- match detection + scoring ----------

	@Test
	void findMatchesHorizontalTripletClearsAndScores() {
		// Three same colour blocks horizontally at row 10, columns 2..4.
		board.newField[2][10] = 5;
		board.newField[3][10] = 5;
		board.newField[4][10] = 5;
		board.level = 0;
		board.noChanges = true;

		board.findMatches();

		assertFalse(board.noChanges, "match should flip noChanges");
		assertEquals(1, board.figuresMatchedCounter);
		assertEquals(10L, board.Score);
		assertEquals(1, listener.tripletCalls.size());
		// matched cells are cleared in oldField
		assertEquals(0, board.oldField[2][10]);
		assertEquals(0, board.oldField[3][10]);
		assertEquals(0, board.oldField[4][10]);
	}

	@Test
	void findMatchesVerticalTripletScoresWithLevelMultiplier() {
		board.newField[3][8] = 4;
		board.newField[3][9] = 4;
		board.newField[3][10] = 4;
		board.level = 2;
		board.noChanges = true;

		board.findMatches();

		assertFalse(board.noChanges);
		assertEquals(30L, board.Score, "(level+1)*10 with level=2 -> 30");
	}

	@Test
	void findMatchesDiagonalTripletDetected() {
		board.newField[2][8] = 6;
		board.newField[3][9] = 6;
		board.newField[4][10] = 6;
		board.noChanges = true;

		board.findMatches();

		assertFalse(board.noChanges);
		assertEquals(1, board.figuresMatchedCounter);
	}

	@Test
	void findMatchesNoTripletLeavesStateUntouched() {
		board.newField[2][10] = 1;
		board.newField[3][10] = 2;
		board.newField[4][10] = 3;
		board.noChanges = true;

		board.findMatches();

		assertTrue(board.noChanges);
		assertEquals(0, board.figuresMatchedCounter);
		assertEquals(0L, board.Score);
		assertTrue(listener.tripletCalls.isEmpty());
	}

	// ---------- collapse / packField ----------

	@Test
	void collapsePacksClearedCellsAndNotifiesListener() {
		// Set up: column 3 has [_, _, _, _, _, 2, 5, 5, 5] from row 7..10.
		board.newField[3][7] = 2;
		board.newField[3][8] = 5;
		board.newField[3][9] = 5;
		board.newField[3][10] = 5;
		board.findMatches(); // clears 3,8 / 3,9 / 3,10 in oldField

		board.DScore = 7;
		board.Score = 100;
		board.collapse();

		// after packField the surviving block (value 2) should sit at the bottom row of its column
		assertEquals(2, board.newField[3][GameConfig.DEPTH]);
		// the rows above bottom should be empty
		for (int r = 1; r < GameConfig.DEPTH; r++) {
			assertEquals(0, board.newField[3][r], "row " + r + " expected empty");
		}
		// listener got field + score callbacks
		assertEquals(1, listener.fieldUpdateCount);
		assertEquals(1, listener.scoreUpdates.size());
		assertEquals(100L + 7L, board.Score, "Score set after findMatches; collapse adds DScore");
	}

	// ---------- level changes ----------

	@Test
	void levelDoesNotChangeBelowThreshold() {
		board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD - 1;
		board.level = 1;
		// collapse() -> changeLevelIfNeeded
		board.collapse();
		assertEquals(1, board.level);
		assertTrue(listener.levelChanges.isEmpty());
	}

	@Test
	void levelIncreasesWhenThresholdReached() {
		board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
		board.level = 1;
		board.collapse();
		assertEquals(2, board.level);
		assertEquals(0, board.figuresMatchedCounter, "counter resets on level up");
		assertEquals(1, listener.levelChanges.size());
		assertEquals(2, (int) listener.levelChanges.get(0));
	}

	@Test
	void levelDoesNotExceedMax() {
		board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD + 5;
		board.level = GameConfig.MAX_LEVEL;
		board.collapse();
		assertEquals(GameConfig.MAX_LEVEL, board.level);
		// listener still fired with capped level
		assertEquals(GameConfig.MAX_LEVEL, (int) listener.levelChanges.get(0));
	}
}
