package com.diozero.aoc.geometry;

import java.util.ArrayList;
import java.util.List;

public record Point4D(int x, int y, int z, int w) {

	private static final List<Point4D> ADJACENT_DELTAS;
	static {
		ADJACENT_DELTAS = new ArrayList<>();

		for (int w = -1; w <= 1; w++) {
			for (int z = -1; z <= 1; z++) {
				for (int y = -1; y <= 1; y++) {
					for (int x = -1; x <= 1; x++) {
						if (x == 0 && y == 0 && z == 0 && w == 0) {
							continue;
						}
						ADJACENT_DELTAS.add(new Point4D(x, y, z, w));
					}
				}
			}
		}
	}

	public Point4D delta(Point4D other) {
		return new Point4D(other.x - x, other.y - y, other.z - z, other.w - w);
	}

	public boolean isAdjacentTo(Point4D other) {
		return ADJACENT_DELTAS.contains(delta(other));
	}
}
