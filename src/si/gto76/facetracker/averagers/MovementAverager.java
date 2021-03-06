package si.gto76.facetracker.averagers;

import java.awt.Color;
import java.util.LinkedList;

import org.opencv.core.Point;

public class MovementAverager extends Averager<Point> {
	
	private static final int MOVE_TRESHOLD = 2;

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
		Point move = new Point(average.x * (2.0/size), average.y * (2.0/size));
		if (getLength(move) < MOVE_TRESHOLD) {
			return new Point(0, 0);
		}
		return move;
	}
	
	private int getLength(Point point) {
		return (int) Math.sqrt(Math.pow(point.x, 2) + Math.pow(point.y, 2));
	}

}
