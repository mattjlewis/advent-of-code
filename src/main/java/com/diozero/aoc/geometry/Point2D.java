package com.diozero.aoc.geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public record Point2D(int x, int y) {
	public enum Axis {
		X, Y;
	}

	public static final Point2D ORIGIN = new Point2D(0, 0);

	public static Point2D sum(List<Point2D> points) {
		return ORIGIN.translate(points);
	}

	public static Point2D parse(String s) {
		final String[] parts = s.split(",");
		return new Point2D(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}

	public static Rectangle getBounds(final Collection<Point2D> points) {
		return getBounds(points.stream());
	}

	public static Rectangle getBounds(final Stream<Point2D> points) {
		int min_x = Integer.MAX_VALUE;
		int max_x = Integer.MIN_VALUE;
		int min_y = Integer.MAX_VALUE;
		int max_y = Integer.MIN_VALUE;
		final Iterator<Point2D> it = points.iterator();
		while (it.hasNext()) {
			final Point2D p = it.next();
			min_x = Math.min(min_x, p.x());
			max_x = Math.max(max_x, p.x());
			min_y = Math.min(min_y, p.y());
			max_y = Math.max(max_y, p.y());
		}

		return Rectangle.create(min_x, min_y, max_x, max_y);
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

	public int manhattanDistance(Point2D other) {
		return manhattanDistance(other.x, other.y);
	}

	public int manhattanDistance(int otherX, int otherY) {
		return Math.abs(otherX - x) + Math.abs(otherY - y);
	}

	public double distance(Point2D other) {
		int delta_x = x - other.x;
		int delta_y = y - other.y;
		return Math.sqrt(delta_x * delta_x + delta_y * delta_y);
	}

	public Point2D rotateDegrees(Axis axis, int degrees) {
		return rotateTurns(axis, degrees / 90);
	}

	public Point2D rotate90(Axis axis) {
		return rotateTurns(axis, 1);
	}

	public Point2D rotateTurns(Axis axis, int numTurns) {
		int cos = COS_VALUES[numTurns % 4];
		int sin = SIN_VALUES[numTurns % 4];

		return new Point2D( //
				x * cos - y * sin, //
				x * sin + y * cos);
	}

	public Point2D translate(Point2D delta) {
		return translate(delta.x, delta.y);
	}

	public Point2D translate(int dx, int dy) {
		return new Point2D(x + dx, y + dy);
	}

	public Point2DLong translate(long dx, long dy) {
		return new Point2DLong(x + dx, y + dy);
	}

	public Point2D delta(Point2D other) {
		return new Point2D(other.x - x, other.y - y);
	}

	public Point2D move(CompassDirection direction) {
		return translate(direction.delta());
	}

	public Point2D move(CompassDirection direction, int amount) {
		return translate(direction.delta().scale(amount));
	}

	public Point2D translate(List<Point2D> deltas) {
		/*
		 * This would be more efficient for large delta sets as it avoids instantiating a lot of
		 * Point2D instances:
		 *
		 * return new MutablePoint2D(x, x).translate(deltas).toPoint2D();
		 */
		return deltas.stream().reduce(this, (a, b) -> a.translate(b));
	}

	public Point2D scale(int amount) {
		return new Point2D(x * amount, y * amount);
	}

	public MutablePoint2D mutable() {
		return new MutablePoint2D(x, y);
	}

	public double angleTo(Point2D other) {
		return angleTo(other, 0);
	}

	public double angleTo(Point2D other, int offset) {
		double result = (Math.toDegrees(Math.atan2(other.y - y, other.x - x)) + offset) % 360;
		return result < 0 ? result + 360 : result;
	}

	public Integer identity(int width) {
		return Integer.valueOf(y * width + x);
	}

	public boolean inBounds(int width, int height) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public boolean inBounds(int x1, int y1, int x2, int y2) {
		return !(x < x1 || y < y1 || x >= x2 || y >= y2);
	}

	public static long area(List<Point2D> vertices) {
		// Shoelace theorem: https://en.wikipedia.org/wiki/Shoelace_formula

		long shoelace_area = 0;
		long boundaries = 0;
		for (int i = 0; i < vertices.size(); i++) {
			final Point2D a = vertices.get(i);
			final Point2D b = vertices.get((i + 1) % vertices.size());
			shoelace_area += a.x * (long) b.y - b.x * (long) a.y;
			boundaries += a.manhattanDistance(b);
		}

		return (shoelace_area + boundaries) / 2 + 1;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public Point2D wrap(int width, int height) {
		return new Point2D(((x % width) + width) % width, ((y % height) + height) % height);
	}
}
