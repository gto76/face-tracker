package si.gto76.facetracker.averagers;

import java.util.LinkedList;

public class CountAverager extends Averager<Integer> {

	public CountAverager(int windowSize) {
		super(windowSize);
	}

	@Override
	protected Integer calculateAverage(LinkedList<Integer> values) {
		int size = values.size();
		
		int sum = 0;
		for (int value: values) {
			sum += value;
		}
		return (int) (long) Math.round((double)sum /size);
		
//		double average = 0;
//		for (int i = 1; i <= size; i++) {
//			double factor = (2.0 * i - 1) / (2 * size);
//			average += factor * values.get(i - 1);
//		}
//		return (int) (long) Math.round(average * (2.0/size));
	}
}
