package com.diozero.aoc.util;

/**
 * Simple 2D line class that only supports horizontal, vertical or diagonal
 * lines. Diagonal lines can only be at an angle of 45 degrees.
 */
public record Line2D(int x1, int y1, int x2, int y2, Line2D.Direction direction) {
	public enum Direction {
		HORIZONTAL, VERTICAL, DIAGONAL;
	}

	public static Line2D create(String s) {
		String[] parts = s.split(" -> ");

		String[] x1_y1 = parts[0].split(",");
		int x1 = Integer.parseInt(x1_y1[0]);
		int y1 = Integer.parseInt(x1_y1[1]);

		String[] x2_y2 = parts[1].split(",");
		int x2 = Integer.parseInt(x2_y2[0]);
		int y2 = Integer.parseInt(x2_y2[1]);

		Direction direction;
		if (x1 != x2 && y1 == y2) {
			direction = Direction.HORIZONTAL;
			// Normalise so that x1 is less than x2
			if (x1 > x2) {
				int tmp = x1;
				x1 = x2;
				x2 = tmp;
			}
		} else if (x1 == x2 && y1 != y2) {
			direction = Direction.VERTICAL;
			// Normalise so that y1 is less than y2
			if (y1 > y2) {
				int tmp = y1;
				y1 = y2;
				y2 = tmp;
			}
		} else {
			direction = Direction.DIAGONAL;
		}

		return new Line2D(x1, y1, x2, y2, direction);
	}

	public boolean isHorizontal() {
		return direction == Direction.HORIZONTAL;
	}

	public boolean isVertical() {
		return direction == Direction.VERTICAL;
	}

	public boolean isDiagonal() {
		return direction == Direction.DIAGONAL;
	}

	public boolean goesForwards() {
		return x1 < x2;
	}

	public boolean goesBackwards() {
		return x1 > x2;
	}

	public boolean goesUp() {
		return y1 < y2;
	}

	public boolean goesDown() {
		return y1 > y2;
	}
}
