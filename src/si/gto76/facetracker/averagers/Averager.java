package si.gto76.facetracker.averagers;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import si.gto76.facetracker.MyColor;

public abstract class Averager<T> {
	private final int windowSize;

	private Map<MyColor, LinkedList<T>> windowValues = new HashMap<MyColor, LinkedList<T>>();

	public Averager(int windowSize) {
		this.windowSize = windowSize;
	}

	public Map<MyColor, T> tick(Map<MyColor, T> newValues) {
		updateValues(newValues);
		Map<MyColor, T> averages = getAverages();
		moveWindow();
		return averages;
	}
	
	protected abstract T calculateAverage(LinkedList<T> values);

	// /////////////////////
	// // UPDATE VALUES ////
	// /////////////////////

	protected void updateValues(Map<MyColor, T> newValues) {
		Set<MyColor> allTheColors = new HashSet<MyColor>(windowValues.keySet());
		allTheColors.addAll(newValues.keySet());

		for (MyColor color : allTheColors) {
			boolean colorIsNew = !windowValues.containsKey(color);
			boolean colorIsNotAmongNewData = !newValues.containsKey(color);

			if (colorIsNew) {
				addNewColor(color, newValues.get(color));
			} else if (colorIsNotAmongNewData) {
				addNull(color);
			} else { // color is both among new and window values
				addValue(color, newValues.get(color));
			}
		}
	}

	private void addNewColor(MyColor color, T value) {
		LinkedList<T> values = getListWithNulls();
		values.add(value);
		windowValues.put(color, values);
	}

	private LinkedList<T> getListWithNulls() {
		LinkedList<T> list = new LinkedList<T>();
		for (int i = 0; i < windowSize - 1; i++) {
			list.add(null);
		}
		return list;
	}

	private void addNull(MyColor color) {
		LinkedList<T> values = windowValues.get(color);
		values.add(null);
	}

	private void addValue(MyColor color, T value) {
		LinkedList<T> values = windowValues.get(color);
		values.add(value);
	}

	// ////////////////////
	// // GET AVERAGES ////
	// ////////////////////

	protected Map<MyColor, T> getAverages() {
		Map<MyColor, T> averages = new HashMap<MyColor, T>();

		Iterator<Entry<MyColor, LinkedList<T>>> iter = windowValues.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<MyColor, LinkedList<T>> entry = iter.next();
			LinkedList<T> values = entry.getValue();
			T average = getAverage(values);
			// If all window values of the color are null, then remove this color
			if (average == null) {
				iter.remove();
				continue;
			}
			averages.put(entry.getKey(), average);
		}
		return averages;
	}

	/**
	 * Returns null if list is empty or if all elements are null.
	 */
	private T getAverage(LinkedList<T> values) {
		values = removeNulls(values);
		int size = values.size();
		if (size == 0) {
			return null;
		}
		return calculateAverage(values);
	}


	private LinkedList<T> removeNulls(LinkedList<T> values) {
		LinkedList<T> newValues = new LinkedList<T>(values);
		List<T> listWithNull = new ArrayList<T>();
		listWithNull.add(null);
		newValues.removeAll(listWithNull);
		return newValues;
	}

	// ///////////////////
	// // MOVE WINDOW ////
	// ///////////////////
	
	protected void moveWindow() {
		for (LinkedList<T> values: windowValues.values()) {
			values.removeFirst();
		}
	}
}