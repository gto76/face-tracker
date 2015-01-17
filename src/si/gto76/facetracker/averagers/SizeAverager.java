package si.gto76.facetracker.averagers;

import java.util.LinkedList;

public class SizeAverager extends Averager<Double> {

	public SizeAverager(int windowSize) {
		super(windowSize);
	}

	@Override
	protected Double calculateAverage(LinkedList<Double> values) {
		int size = values.size();
		double average = 0;
		for (int i = 1; i <= size; i++) {
			double factor = (2.0 * i - 1) / (2 * size);
			average += factor * values.get(i - 1);
		}
		return average * (2.0/size);
	}

}
