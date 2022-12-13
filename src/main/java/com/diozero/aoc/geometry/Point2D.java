package com.diozero.aoc.geometry;

import java.util.List;

public record Point2D(int x, int y) {
	public enum Axis {
		X, Y;
	}

	public static final Point2D ORIGIN = new Point2D(0, 0);

	public static Point2D sum(List<Point2D> points) {
		return ORIGIN.translate(points);
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
		int deltaX = x - other.x;
		int deltaY = y - other.y;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
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
		return new Point2D(x + delta.x, y + delta.y);
	}

	public Point2D translate(CompassDirection direction) {
		return translate(direction.delta());
	}

	public Point2D translate(List<Point2D> deltas) {
		/*
		 * This would be more efficient for large delta sets as it avoids instantiating
		 * a lot of Point2D instances:
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

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
