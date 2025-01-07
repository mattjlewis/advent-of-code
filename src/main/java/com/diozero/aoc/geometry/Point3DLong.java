package com.diozero.aoc.geometry;

public record Point3DLong(long x, long y, long z) {

	public static final Point3DLong ORIGIN = new Point3DLong(0, 0, 0);

	public static Point3DLong parse(String s) {
		final String[] parts = s.split(",");
		return new Point3DLong(Long.parseLong(parts[0].trim()), Long.parseLong(parts[1].trim()),
				Long.parseLong(parts[2].trim()));
	}
}
