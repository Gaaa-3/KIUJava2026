package columns.model.fakes;

import columns.model.kernel.RandomGenerator;

/** Deterministic RandomGenerator: returns a fixed sequence, cycling if exhausted. */
public class FakeRandom implements RandomGenerator {
	private final int[] seq;
	private int i = 0;

	public FakeRandom(int... seq) {
		this.seq = seq.length == 0 ? new int[] { 0 } : seq;
	}

	@Override
	public int nextInt() {
		int v = seq[i % seq.length];
		i++;
		return v;
	}
}
