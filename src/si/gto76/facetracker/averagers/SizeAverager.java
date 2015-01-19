package si.gto76.facetracker.averagers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import si.gto76.facetracker.MyColor;

public class SizeAverager extends Averager<Double> {

	public SizeAverager(int windowSize) {
		super(windowSize);
	}

	/**
	 * Modified tick mehtod, so that averager doesen't pass on the values that are were not present aong
	 * newValues
	 */
	@Override
	public Map<MyColor, Double> tick(Map<MyColor, Double> newValues) {

		Map<MyColor, Double> tick = super.tick(newValues);

		Iterator<Entry<MyColor, Double>> iter = tick.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<MyColor, Double> entry = iter.next();
			MyColor color = entry.getKey();
			if (!newValues.containsKey(color)) {
				iter.remove();
			}
		}
		return tick;
	}

	@Override
	protected Double calculateAverage(LinkedList<Double> values) {
		int size = values.size();
		double average = 0;
		for (int i = 1; i <= size; i++) {
			double factor = (2.0 * i - 1) / (2 * size);
			average += factor * values.get(i - 1);
		}
		return average * (2.0 / size);
	}

}
