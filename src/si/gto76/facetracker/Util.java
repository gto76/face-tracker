package si.gto76.facetracker;

import org.opencv.core.Point;
import org.opencv.core.Rect;

public class Util {
	
	static Point getCentroide(Rect rect) {
		return new Point(rect.x + rect.width/2, rect.y + rect.height/2);
	}

}
