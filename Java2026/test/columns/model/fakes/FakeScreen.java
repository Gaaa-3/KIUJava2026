package columns.model.fakes;

import java.util.ArrayList;
import java.util.List;

import columns.model.kernel.Screen;

/** Recording Screen test double. Tests never need pixels — only that draw calls happened. */
public class FakeScreen implements Screen {
	public final List<String> calls = new ArrayList<>();
	public int lastColor = -1;
	public int fillRectCount = 0;
	public int drawRectCount = 0;
	public int drawStringCount = 0;

	@Override public void setColor(int color) { lastColor = color; calls.add("setColor(" + color + ")"); }
	@Override public void fillRect(int x, int y, int w, int h) { fillRectCount++; calls.add("fillRect(" + x + "," + y + "," + w + "," + h + ")"); }
	@Override public void drawRect(int x, int y, int w, int h) { drawRectCount++; calls.add("drawRect(" + x + "," + y + "," + w + "," + h + ")"); }
	@Override public void drawString(String s, int x, int y) { drawStringCount++; calls.add("drawString(" + s + "," + x + "," + y + ")"); }
	@Override public void clearRect(int x, int y, int w, int h) { calls.add("clearRect"); }
	@Override public int Black() { return 0; }
	@Override public int White() { return 8; }
}
