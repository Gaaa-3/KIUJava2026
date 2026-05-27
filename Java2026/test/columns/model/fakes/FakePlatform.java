package columns.model.fakes;

import java.util.ArrayDeque;
import java.util.Deque;

import columns.model.GameEvent;
import columns.model.kernel.Platform;
import columns.model.kernel.RandomGenerator;
import columns.model.kernel.Screen;

/**
 * Fully controllable Platform test double.
 *
 * - currentTime() advances deterministically (no wall-clock).
 * - delay() does NOT sleep; it just records the requested ms.
 * - getEvent() returns the next queued GameEvent.
 * - isKeyPressed() can be scripted as a sequence of booleans (used by PAUSE loop test).
 */
public class FakePlatform implements Platform {

	public final FakeScreen screen = new FakeScreen();
	public final FakeRandom random;

	public long now = 0;
	public long tc = 0;
	public long totalDelay = 0;
	public int delayCalls = 0;

	private final Deque<GameEvent> events = new ArrayDeque<>();
	private final Deque<Boolean> keyPressedScript = new ArrayDeque<>();
	private boolean keyPressed = false;
	private int keyCode = 0;

	public FakePlatform() { this(new FakeRandom(0)); }
	public FakePlatform(FakeRandom random) { this.random = random; }

	public void queueEvent(GameEvent e) { events.add(e); keyPressed = true; }
	public void scriptKeyPressed(Boolean... vals) { for (Boolean b : vals) keyPressedScript.add(b); }

	@Override public void delay(long t) { delayCalls++; totalDelay += t; now += t; }
	@Override public long currentTime() { return now; }

	@Override
	public boolean isKeyPressed() {
		if (!keyPressedScript.isEmpty()) return keyPressedScript.poll();
		return keyPressed;
	}

	@Override public void setKeyPressed(boolean v) { keyPressed = v; }
	@Override public Screen getScreen() { return screen; }
	@Override public long getTc() { return tc; }
	@Override public void setTc(long t) { tc = t; }
	@Override public int getKeyPressed() { return keyCode; }

	@Override
	public GameEvent getEvent() {
		GameEvent e = events.poll();
		return e == null ? GameEvent.NONE : e;
	}

	@Override public RandomGenerator getRandomGenerator() { return random; }
}
