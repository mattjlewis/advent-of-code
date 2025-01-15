package com.diozero.aoc.geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public record Point2DLong(long x, long y) {
	public enum Axis {
		X, Y;
	}

	public static final Point2DLong ORIGIN = new Point2DLong(0, 0);

	public static Point2DLong sum(List<Point2DLong> points) {
		return ORIGIN.translate(points);
	}

	public static Point2DLong parse(String s) {
		final String[] parts = s.split(",");
		return new Point2DLong(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
	}

	public static RectangleLong getBounds(final Collection<Point2DLong> points) {
		return getBounds(points.stream());
	}

	public static RectangleLong getBounds(final Stream<Point2DLong> points) {
		long min_x = Long.MAX_VALUE;
		long max_x = Long.MIN_VALUE;
		long min_y = Long.MAX_VALUE;
		long max_y = Long.MIN_VALUE;
		final Iterator<Point2DLong> it = points.iterator();
		while (it.hasNext()) {
			final Point2DLong p = it.next();
			min_x = Math.min(min_x, p.x());
			max_x = Math.max(max_x, p.x());
			min_y = Math.min(min_y, p.y());
			max_y = Math.max(max_y, p.y());
		}

		return RectangleLong.create(min_x, min_y, max_x, max_y);
	}

	private static final int[] COS_VALUES;
	private static final int[] SIN_VALUES;
	static {
		COS_VALUES = new int[4];
		SIN_VALUES = new int[4];
		for (int amount = 0; amount < COS_VALUES.length; amount++) {
			COS_VALUES[amount] = (int) Math.round(Math.cos(Math.PI / 2 * amount));
			SIN_VALUES[amount] = (int) Math.round(Math.sin(Math.PI / 2 * amount));
		}
	}

	public long manhattanDistance(Point2DLong other) {
		return manhattanDistance(other.x, other.y);
	}

	public long manhattanDistance(long otherX, long otherY) {
		return Math.abs(otherX - x) + Math.abs(otherY - y);
	}

	public double distance(Point2DLong other) {
		long deltaX = x - other.x;
		long deltaY = y - other.y;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

	public Point2DLong rotateDegrees(Axis axis, int degrees) {
		return rotateTurns(axis, degrees / 90);
	}

	public Point2DLong rotate90(Axis axis) {
		return rotateTurns(axis, 1);
	}

	public Point2DLong rotateTurns(Axis axis, int numTurns) {
		int cos = COS_VALUES[numTurns % 4];
		int sin = SIN_VALUES[numTurns % 4];

		return new Point2DLong( //
				x * cos - y * sin, //
				x * sin + y * cos);
	}

	public Point2DLong translate(Point2D delta) {
		return translate(delta.x(), delta.y());
	}

	public Point2DLong translate(Point2DLong delta) {
		return translate(delta.x, delta.y);
	}

	public Point2DLong translate(long dx, long dy) {
		return new Point2DLong(x + dx, y + dy);
	}

	public Point2DLong delta(Point2D other) {
		return new Point2DLong(other.x() - x, other.y() - y);
	}

	public Point2DLong delta(Point2DLong other) {
		return new Point2DLong(other.x - x, other.y - y);
	}

	public Point2DLong move(CompassDirection direction) {
		return translate(direction.delta());
	}

	public Point2DLong move(CompassDirection direction, int amount) {
		return translate(direction.delta().scale(amount));
	}

	public Point2DLong translate(List<Point2DLong> deltas) {
		/*
		 * This would be more efficient for large delta sets as it avoids instantiating a lot of
		 * Point2D instances:
		 *
		 * return new MutablePoint2D(x, x).translate(deltas).toPoint2D();
		 */
		return deltas.stream().reduce(this, (a, b) -> a.translate(b));
	}

	public Point2DLong scale(long amount) {
		return new Point2DLong(x * amount, y * amount);
	}

	public boolean inBounds(long width, long height) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public boolean inBounds(long x1, long y1, long x2, long y2) {
		return !(x < x1 || y < y1 || x >= x2 || y >= y2);
	}

	public Point2DLong wrap(long width, long height) {
		return new Point2DLong(((x % width) + width) % width, ((y % height) + height) % height);
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
