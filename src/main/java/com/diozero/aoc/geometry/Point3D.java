package com.diozero.aoc.geometry;

import java.util.ArrayList;
import java.util.List;

public record Point3D(int x, int y, int z) {
	public enum Axis {
		X, Y, Z;
	}

	public static final Point3D ORIGIN = new Point3D(0, 0, 0);

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

	private static final List<Point3D> ADJACENT_DELTAS;
	static {
		ADJACENT_DELTAS = new ArrayList<>();

		for (int z = -1; z <= 1; z++) {
			for (int y = -1; y <= 1; y++) {
				for (int x = -1; x <= 1; x++) {
					if (x == 0 && y == 0 && z == 0) {
						continue;
					}
					ADJACENT_DELTAS.add(new Point3D(x, y, z));
				}
			}
		}
	}

	public Point3D delta(Point3D other) {
		return new Point3D(other.x - x, other.y - y, other.z - z);
	}

	public int manhattanDistance(Point3D p) {
		return Math.abs(x - p.x) + Math.abs(y - p.y) + Math.abs(z - p.z);
	}

	public Point3D translate(Point3D delta) {
		return new Point3D(delta.x + x, delta.y + y, delta.z + z);
	}

	public boolean isAdjacentTo(Point3D other) {
		return ADJACENT_DELTAS.contains(delta(other));
	}

	public Point3D rotate90(Axis axis) {
		return rotate(axis, 1);
	}

	public Point3D rotate(Axis axis, int amount) {
		int[][] matrix;

		int cos = COS_VALUES[amount % 4];
		int sin = SIN_VALUES[amount % 4];

		switch (axis) {
		case X:
			matrix = new int[][] { //
					{ 1, 0, 0 }, //
					{ 0, cos, -sin }, //
					{ 0, sin, cos } //
			};
			break;
		case Y:
			matrix = new int[][] { //
					{ cos, 0, sin }, //
					{ 0, 1, 0 }, //
					{ -sin, 0, cos } //
			};
			break;
		case Z:
			matrix = new int[][] { //
					{ cos, -sin, 0 }, //
					{ sin, cos, 0 }, //
					{ 0, 0, 1 } //
			};
			break;
		default:
			throw new IllegalArgumentException("Unhandled rotation axis " + axis);
		}

		return new Point3D( //
				matrix[0][0] * x + matrix[0][1] * y + matrix[0][2] * z, //
				matrix[1][0] * x + matrix[1][1] * y + matrix[1][2] * z, //
				matrix[2][0] * x + matrix[2][1] * y + matrix[2][2] * z);
	}

	public MutablePoint3D mutable() {
		return new MutablePoint3D(x, y, z);
	}
}
