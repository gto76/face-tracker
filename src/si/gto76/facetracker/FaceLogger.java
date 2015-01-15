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
import org.opencv.core.Rect;

public class FaceLogger {
	public static long AGE_LIMIT_MILLIS = 1000;

	List<Face> faces = new ArrayList<Face>();
	private long lastCycleTime = 0;
	
	static final Random RAND = new Random();

	public void tick(MatOfRect areas) {
		lastCycleTime = System.currentTimeMillis();

		removeOldFaces();

		// find nearest face of all the areas
		Map<Rect, Face> nearestFaces = findNearestFaces(areas);

		// check where face occurs more than one and add face to closer area
		enforceSingleFacePerArea(nearestFaces);

		// for areas without the face create new face
		createNewFacesForAreasWithoutAndUpdateOldOnes(nearestFaces);
	}

	private void removeOldFaces() {
		Iterator<Face> i = faces.iterator();
		while (i.hasNext()) {
			Face face = i.next();
			if (face.age() > AGE_LIMIT_MILLIS) {
				i.remove();
			}
		}
	}

	// /////////////////////////////////////////////
	// /// FIND NEAREST FACE OF ALL THE AREAS //////
	// /////////////////////////////////////////////

	private Map<Rect, Face> findNearestFaces(MatOfRect areas) {
		Map<Rect, Face> nearestFaces = new HashMap<Rect, Face>();
		for (Rect area : areas.toArray()) {
			Map<Double, Face> distanceMap = new HashMap<Double, Face>();
			for (Face face : faces) {
				Double distance = getDistance(area, face);
				distanceMap.put(distance, face);
			}
			List<Double> distances = new ArrayList<>(distanceMap.keySet());
			if (distances.size() == 0) {
				nearestFaces.put(area, null);
			} else {
				Collections.sort(distances);
				Face nearestFace = distanceMap.get(distances.get(0));
				nearestFaces.put(area, nearestFace);
			}
		}
		return nearestFaces;
	}

	private Double getDistance(Rect area, Face face) {
		double dx = area.x - face.area.x;
		double dy = area.y - face.area.y;
		double dz = Math.sqrt(area.area() - face.area.area());
		double dt = lastCycleTime - face.lastSeen;
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2) + Math.pow(dt, 2));
	}

	// ///////////////////////////////////////
	// /// ENFORCE SINGLE FACE PER AREA //////
	// ///////////////////////////////////////

	private void enforceSingleFacePerArea(Map<Rect, Face> nearestFaces) {
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

	// ////////////////////////////////////////////////////
	// /// CREATE NEW FACES FOR AREAS WITHOUT A FACE //////
	// ////////////////////////////////////////////////////

	private void createNewFacesForAreasWithoutAndUpdateOldOnes(Map<Rect, Face> nearestFaces) {
		for (Rect rect : nearestFaces.keySet()) {
			Face nearestFace = nearestFaces.get(rect);
			if (nearestFace == null) {
				Face face = new Face(rect, lastCycleTime, getRandomColor());
				faces.add(face);
			} else {
				nearestFace.area = rect;
				nearestFace.lastSeen = lastCycleTime;
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

	// ///////

	public int getNoOfFaces() {
		int noOfFaces = 0;
		for (Face face : faces) {
			if (face.lastSeen == lastCycleTime) {
				noOfFaces++;
			}
		}
		return noOfFaces;
	}
	
	/////////
	
	public Map<MyColor,Double> getFaceSizes() {
		Map<MyColor,Double> sizes = new HashMap<MyColor, Double>();
		for (Face face: faces) {
			sizes.put(face.color, face.area.area());
		}
		return sizes;
	}

}

class Face {
	Rect area;
	long lastSeen;
	MyColor color;

	public Face(Rect area, long lastSeen, MyColor color) {
		this.area = area;
		this.lastSeen = lastSeen;
		this.color = color;
	}

	public long age() {
		long currentTimeMillis = System.currentTimeMillis();
		return currentTimeMillis - lastSeen;
	}
}
