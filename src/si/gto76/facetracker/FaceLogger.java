package si.gto76.facetracker;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;

public class FaceLogger {
	public static long AGE_LIMIT_MILLIS = 1000;
	private static final double TIME_WEIGHT = 0.1;
	private static final double AREA_WEIGHT = 0.002;

	List<Face> faces = new ArrayList<Face>();
	private long lastCycleTime = 0;

	static final Random RAND = new Random();

	// /////////////////////////
	// /// THE TICK METHOD /////
	// /////////////////////////

	public void tick(MatOfRect rects) {
		lastCycleTime = System.currentTimeMillis();
		removeOldFaces();
		// find nearest face for all the rectangles
		Map<Rect, Face> nearestFaces = findNearestFaces(rects);
		// check where face occurs more than one and add face to closer rectangle
		enforceSingleFacePerRect(nearestFaces);
		createNewFacesForRectsWithoutAndUpdateOldOnes(nearestFaces);
		removeGlitchFaces();
		printAllFaces();
	}

	private void removeOldFaces() {
		Iterator<Face> i = faces.iterator();
		while (i.hasNext()) {
			Face face = i.next();
			if (face.getMillisSinceLost() > AGE_LIMIT_MILLIS) {
				i.remove();
			}
		}
	}

	/**
	 * Removes faces that were created last cycle and have no Rect already in this one.
	 */
	private void removeGlitchFaces() {
		Iterator<Face> i = faces.iterator();
		while (i.hasNext()) {
			Face face = i.next();
			if (face.iterations == 0 && face.lastSeen != lastCycleTime) {
				i.remove();
			}
		}
	}

	private void printAllFaces() {
		for (Face face : faces) {
			printFace(face);
		}
	}

	private void printFace(Face face) {
		System.out.println("#### face " + face.color.c.getBlue());
		System.out.println("t " + face.getAdjustedTimeSinceLost());
		System.out.println("x " + face.getCentroid().x);
		System.out.println("y " + face.getCentroid().y);
		System.out.println("z " + face.getAdjustedSize());
		System.out.println("dx " + face.getDirection().x);
		System.out.println("dy " + face.getDirection().y);
		System.out.println();
	}

	// //////////////////////////////////////////////////
	// /// FIND NEAREST FACE OF ALL THE RECTANGLES //////
	// //////////////////////////////////////////////////

	private Map<Rect, Face> findNearestFaces(MatOfRect rects) {
		Map<Rect, Face> nearestFaces = new HashMap<Rect, Face>();
		for (Rect rect : rects.toArray()) {
			Map<Double, Face> distanceMap = new HashMap<Double, Face>();
			for (Face face : faces) {
				Double distance = getDistance(rect, face);
				distanceMap.put(distance, face);
			}
			List<Double> distances = new ArrayList<>(distanceMap.keySet());
			if (distances.size() == 0) {
				nearestFaces.put(rect, null);
			} else {
				Collections.sort(distances);
				Face nearestFace = distanceMap.get(distances.get(0));
				nearestFaces.put(rect, nearestFace);
			}
		}
		return nearestFaces;
	}

	private Double getDistance(Rect rect, Face face) {
		Point rectCent = Util.getCentroide(rect);
		Point faceCent = face.getCentroid();
		double dx = rectCent.x - faceCent.x;
		double dy = rectCent.y - faceCent.y;
		double dz = (rect.area() - face.getRect().area()) * AREA_WEIGHT;
		double dt = face.getAdjustedTimeSinceLost();
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2) + Math.pow(dt, 2));
	}

	// ////////////////////////////////////////////
	// /// ENFORCE SINGLE FACE PER RECTANGLE //////
	// ////////////////////////////////////////////

	private void enforceSingleFacePerRect(Map<Rect, Face> nearestFaces) {
		Map<Face, Integer> occurances = countOccurances(nearestFaces);
		for (Entry<Face, Integer> faceWithCount : occurances.entrySet()) {
			if (faceWithCount.getValue() > 1) {
				Face face = faceWithCount.getKey();
				Rect nearestRect = getNearestRect(face, nearestFaces.keySet());
				asignFaceOnlyToNearestRect(nearestFaces, face, nearestRect);
			}
		}
	}

	private Map<Face, Integer> countOccurances(Map<Rect, Face> nearestFaces) {
		Map<Face, Integer> occurances = new HashMap<Face, Integer>();
		for (Face face : nearestFaces.values()) {
			if (face == null) {
				continue;
			}
			if (occurances.containsKey(face)) {
				occurances.put(face, occurances.get(face) + 1);
			} else {
				occurances.put(face, 1);
			}
		}
		return occurances;
	}

	private Rect getNearestRect(Face face, Set<Rect> rects) {
		Rect nearestRect = null;
		double nearestDistance = Double.MAX_VALUE;
		for (Rect rect : rects) {
			if (nearestRect == null) {
				nearestRect = rect;
				nearestDistance = getDistance(rect, face);
				continue;
			}
			double distance = getDistance(rect, face);
			if (distance < nearestDistance) {
				nearestRect = rect;
				nearestDistance = distance;
			}
		}
		return nearestRect;
	}

	/**
	 * So we get a map like this: Rect1 -> FaceA Rect2 -> FaceB Rect3 -> null
	 */
	private void asignFaceOnlyToNearestRect(Map<Rect, Face> nearestFaces, Face face, Rect nearestRect) {
		for (Entry<Rect, Face> rectAndFace : nearestFaces.entrySet()) {
			Rect rect = rectAndFace.getKey();
			Face faceTmp = rectAndFace.getValue();
			if (faceTmp == face && rect != nearestRect) {
				nearestFaces.put(rect, null);
			}
		}
	}

	// /////////////////////////////////////////////////////////
	// /// CREATE NEW FACES FOR RECTANGLES WITHOUT A FACE //////
	// /////////////////////////////////////////////////////////

	private void createNewFacesForRectsWithoutAndUpdateOldOnes(Map<Rect, Face> nearestFaces) {
		for (Rect rect : nearestFaces.keySet()) {
			Face nearestFace = nearestFaces.get(rect);
			if (nearestFace == null) {
				Face face = new Face(rect, lastCycleTime, getRandomColor());
				faces.add(face);
			} else {
				nearestFace.setRect(rect);
				nearestFace.lastSeen = lastCycleTime;
				nearestFace.iterations++;
			}
		}
	}

	private MyColor getRandomColor() {
		float r = RAND.nextFloat();
		float g = RAND.nextFloat();
		float b = RAND.nextFloat();
		Color randomColor = new Color(r, g, b);
		return new MyColor(randomColor);
	}

	// ////////////////////////////
	// ///// PUBLIC GETTERS ///////
	// ////////////////////////////

	public int getNoOfFaces() {
		int noOfFaces = 0;
		for (Face face : faces) {
			// do not include the newbies, and include the oldies that just got droped
			boolean activeNotNoob = face.lastSeen == lastCycleTime && face.iterations != 0;
			boolean nonactiveFreshman = face.getMillisSinceLost() < 10 && face.iterations > 10;
			boolean nonactiveVeteran = face.getMillisSinceLost() < 500 && face.getAge() > 4000;
			if (activeNotNoob || nonactiveFreshman || nonactiveVeteran) {
				noOfFaces++;
			}
		}
		return noOfFaces;
	}

	public Map<MyColor, Double> getFaceSizes() {
		Map<MyColor, Double> sizes = new HashMap<MyColor, Double>();
		for (Face face : faces) {
			if (face.iterations > 0) {
				sizes.put(face.color, face.getRect().area());
			}
		}
		return sizes;
	}

	public Map<MyColor, Point> getFacePositions() {
		Map<MyColor, Point> positions = new HashMap<MyColor, Point>();
		for (Face face : faces) {
			if (face.iterations > 0) {
				positions.put(face.color, face.getCentroid());
			}
		}
		return positions;
	}

	public Map<MyColor, Point> getFaceMovements() {
		Map<MyColor, Point> movements = new HashMap<MyColor, Point>();
		for (Face face : faces) {
			if (face.iterations > 0) {
				movements.put(face.color, face.getDirection());
			}
		}
		return movements;
	}

	// ////////////////////////
	// ///// FACE CLASS ///////
	// ////////////////////////

	private class Face {
		private Rect rect;
		private Rect lastRect;
		final long created;
		long lastSeen;
		final MyColor color;
		int iterations = 0;

		public Face(Rect rect, long lastSeen, MyColor color) {
			this.rect = rect;
			this.lastRect = rect;
			this.created = lastSeen;
			this.lastSeen = lastSeen;
			this.color = color;
		}

		public Point getDirection() {
			Point newCentr = Util.getCentroide(rect);
			Point oldCentr = Util.getCentroide(lastRect);
			return new Point(newCentr.x - oldCentr.x, newCentr.y - oldCentr.y);
		}

		public void setRect(Rect rect) {
			lastRect = this.rect;
			this.rect = rect;
		}

		public Rect getRect() {
			return rect;
		}

		public Point getCentroid() {
			return Util.getCentroide(rect);
		}

		public long getMillisSinceLost() {
			long currentTimeMillis = System.currentTimeMillis();
			return currentTimeMillis - lastSeen;
		}

		public long getAge() {
			long currentTimeMillis = System.currentTimeMillis();
			return currentTimeMillis - created;
		}
		
		/**
		 *  Milliseconds since lost adjusted
		 */
		public double getAdjustedTimeSinceLost() {
			return (lastCycleTime - lastSeen) * TIME_WEIGHT;
		}
		
		/**
		 *  Rectangle area size adjusted
		 */
		public double getAdjustedSize() {
			return rect.area() * AREA_WEIGHT;
		}
	}
}
