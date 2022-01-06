package com.diozero.aoc.util;

public record Point2D(int x, int y) {
	public enum Axis {
		X, Y;
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
}
