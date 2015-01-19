package si.gto76.facetracker;

import java.awt.Color;

public class MyColor implements Comparable<MyColor> {
	public final Color c;
	
	public MyColor(Color c) {
		this.c = c;
	}

	@Override
	public int compareTo(MyColor other) {
		Integer thisRGB = c.getRGB();
		Integer otherRGB = other.c.getRGB();
		return thisRGB.compareTo(otherRGB);
	}

	@Override
	public String toString() {
		return c.toString();
	}
}
