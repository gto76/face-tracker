package si.gto76.facetracker;

import java.awt.Color;

public class MyColor implements Comparable<MyColor> {
	public final Color c;
	
	public MyColor(Color c) {
		this.c = c;
	}

	@Override
	public int compareTo(MyColor other) {
		if (c.getRGB() == other.c.getRGB()) {
			return 0;
		}
		return 1;
	}

}
