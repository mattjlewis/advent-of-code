package com.diozero.aoc2021.util;

/**
 * Simple 2D line class that only supports horizontal, vertical or diagonal
 * lines at an angle of 45 degrees.
 */
public class Line2D {
	public enum Direction {
		HORIZONTAL, VERTICAL, DIAGONAL;
	}

	private int x1, y1, x2, y2;
	private Direction direction;

	public Line2D(String s) {
		String[] parts = s.split(" -> ");

		String[] x1_y1 = parts[0].split(",");
		x1 = Integer.parseInt(x1_y1[0]);
		y1 = Integer.parseInt(x1_y1[1]);

		String[] x2_y2 = parts[1].split(",");
		x2 = Integer.parseInt(x2_y2[0]);
		y2 = Integer.parseInt(x2_y2[1]);

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
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	public Direction getDirection() {
		return direction;
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

	@Override
	public String toString() {
		return x1 + "," + y1 + " -> " + x2 + "," + y2;
	}
}
