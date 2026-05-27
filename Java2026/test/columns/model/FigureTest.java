package columns.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import columns.model.fakes.FakeRandom;

class FigureTest {

	@Test
	void constructorSeedsThreeColorsFromRandomGenerator() {
		// nextInt() values 0,1,6 -> abs(v)%7+1 = 1,2,7
		Figure f = new Figure(new FakeRandom(0, 1, 6));
		assertEquals(1, f.c[1]);
		assertEquals(2, f.c[2]);
		assertEquals(7, f.c[3]);
		assertEquals(0, f.c[0], "c[0] is unused and must stay 0");
	}

	@Test
	void constructorStartsAtTopCenter() {
		Figure f = new Figure(new FakeRandom(0));
		assertEquals(GameConfig.WIDTH / 2 + 1, f.x);
		assertEquals(1, f.y);
	}

	@Test
	void constructorHandlesNegativeRandomViaAbs() {
		// Integer.MIN_VALUE is the trickiest case for Math.abs, but any negative
		// works for the modulus pattern; we exercise a few negatives.
		Figure f = new Figure(new FakeRandom(-1, -7, -8));
		// abs(-1)%7+1 = 2 ; abs(-7)%7+1 = 1 ; abs(-8)%7+1 = 2
		assertEquals(2, f.c[1]);
		assertEquals(1, f.c[2]);
		assertEquals(2, f.c[3]);
	}

	@Test
	void moveRightIncrementsX() {
		Figure f = new Figure(new FakeRandom(0));
		int x0 = f.x;
		f.moveRight();
		assertEquals(x0 + 1, f.x);
	}

	@Test
	void moveLeftDecrementsX() {
		Figure f = new Figure(new FakeRandom(0));
		int x0 = f.x;
		f.moveLeft();
		assertEquals(x0 - 1, f.x);
	}

	@Test
	void moveDownIncrementsY() {
		Figure f = new Figure(new FakeRandom(0));
		int y0 = f.y;
		f.moveDown();
		assertEquals(y0 + 1, f.y);
	}

	@Test
	void rotateUpCyclesColorsForward() {
		Figure f = new Figure(new FakeRandom(0, 1, 2)); // c=[0,1,2,3]
		f.rotateUp();
		assertArrayEquals(new int[] { 0, 2, 3, 1 }, f.c);
	}

	@Test
	void rotateDownCyclesColorsBackward() {
		Figure f = new Figure(new FakeRandom(0, 1, 2)); // c=[0,1,2,3]
		f.rotateDown();
		assertArrayEquals(new int[] { 0, 3, 1, 2 }, f.c);
	}

	@Test
	void rotateUpThenDownIsIdentity() {
		Figure f = new Figure(new FakeRandom(3, 4, 5));
		int[] before = f.c.clone();
		f.rotateUp();
		f.rotateDown();
		assertArrayEquals(before, f.c);
	}

	@Test
	void rotateUpThreeTimesIsIdentity() {
		Figure f = new Figure(new FakeRandom(0, 1, 2));
		int[] before = f.c.clone();
		f.rotateUp(); f.rotateUp(); f.rotateUp();
		assertArrayEquals(before, f.c);
	}
}
