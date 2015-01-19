package si.gto76.facetracker.averagers;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import si.gto76.facetracker.MyColor;

public class CountAverager extends Averager<Integer> {
	private static final MyColor MY_COLOR = new MyColor(Color.BLACK);
	private final Map<MyColor,Integer> myMap = new HashMap<MyColor, Integer>();

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
	}
	
	
	public Integer tickInteger(Integer value) {
		myMap.put(MY_COLOR, value);
		updateValues(myMap);
		Map<MyColor, Integer> averages = getAverages();
		moveWindow();
		return averages.get(MY_COLOR);
	}
	
}
