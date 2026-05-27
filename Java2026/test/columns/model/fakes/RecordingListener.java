package columns.model.fakes;

import java.util.ArrayList;
import java.util.List;

import columns.model.kernel.ModelListener;

/** Records ModelListener callbacks for assertions. */
public class RecordingListener implements ModelListener {
	public final List<Integer> levelChanges = new ArrayList<>();
	public final List<long[]> tripletCalls = new ArrayList<>();
	public final List<Long> scoreUpdates = new ArrayList<>();
	public int fieldUpdateCount = 0;
	public int[][] lastField;

	@Override public void levelHasChanged(int level) { levelChanges.add(level); }

	@Override
	public void tripletDetected(int a, int b, int c, int d, int i, int j) {
		tripletCalls.add(new long[] { a, b, c, d, i, j });
	}

	@Override public void fieldWasUpdated(int[][] f) { fieldUpdateCount++; lastField = f; }
	@Override public void scoreUpdated(long score) { scoreUpdates.add(score); }
}
