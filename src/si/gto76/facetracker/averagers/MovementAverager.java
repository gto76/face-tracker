package si.gto76.facetracker.averagers;

import java.util.LinkedList;

import org.opencv.core.Point;

public class MovementAverager extends Averager<Point> {

	public MovementAverager(int windowSize) {
		super(windowSize);
	}

	@Override
	protected Point calculateAverage(LinkedList<Point> values) {
		int size = values.size();
		Point average = new Point(0, 0);
		for (int i = 1; i <= size; i++) {
			double factor = (2.0 * i - 1) / (2 * size);
			Point point = values.get(i - 1);
			average = new Point(average.x + point.x*factor, average.y + point.y*factor);
		}
		return new Point(average.x * (2.0/size), average.y * (2.0/size));
	}

}
