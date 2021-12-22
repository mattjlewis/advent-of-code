package com.diozero.aoc.util;

public final class Rotation3D {
	//
	public enum Axis {
		X, Y, Z;
	}

	private Rotation3D() {
	}

	// Self-test
	public static void main(String[] args) {
		Point3D orig = new Point3D(1, 2, 3);

		Point3D p = orig;
		Rotation3D.Axis axis = Rotation3D.Axis.X;
		Point3D rotated = p.rotate90(axis);
		Point3D expected = new Point3D(1, -3, 2);
		System.out.format("%s - rotated %s by 1 on %s to %s, expected %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), p, axis, rotated, expected);
		int amount = 1;
		Point3D rotated2 = orig.rotate(axis, amount);
		System.out.format("%s - rotated %s by %d on %s to %s, expected %s%n",
				(rotated2.equals(expected) ? "Correct!" : "Error!"), orig, amount, axis, rotated2, expected);

		p = rotated;
		rotated = p.rotate90(axis);
		expected = new Point3D(1, -2, -3);
		System.out.format("%s - rotated %s by 1 on %s to %s, expected %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), p, axis, rotated, expected);
		amount++;
		rotated2 = orig.rotate(axis, amount);
		System.out.format("%s - rotated %s by %d on %s to %s, expected %s%n",
				(rotated2.equals(expected) ? "Correct!" : "Error!"), orig, amount, axis, rotated2, expected);

		p = rotated;
		rotated = p.rotate90(axis);
		expected = new Point3D(1, 3, -2);
		System.out.format("%s - rotated %s by 1 on %s to %s, expected %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), p, axis, rotated, expected);
		amount++;
		rotated2 = orig.rotate(axis, amount);
		System.out.format("%s - rotated %s by %d on %s to %s, expected %s%n",
				(rotated2.equals(expected) ? "Correct!" : "Error!"), orig, amount, axis, rotated2, expected);

		p = rotated;
		rotated = p.rotate90(axis);
		expected = orig;
		System.out.format("%s - rotated %s by 1 on %s to %s, expected %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), p, axis, rotated, expected);
		amount++;
		rotated2 = orig.rotate(axis, amount);
		System.out.format("%s - rotated %s by %d on %s to %s, expected %s%n",
				(rotated2.equals(expected) ? "Correct!" : "Error!"), orig, amount, axis, rotated2, expected);

		System.out.println();

		axis = Rotation3D.Axis.Y;
		p = orig;
		rotated = p.rotate90(axis);
		expected = new Point3D(3, 2, -1);
		System.out.format("%s - rotated %s by 1 on %s to %s, expected %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), p, axis, rotated, expected);
		amount = 1;
		rotated2 = orig.rotate(axis, amount);
		System.out.format("%s - rotated %s by %d on %s to %s, expected %s%n",
				(rotated2.equals(expected) ? "Correct!" : "Error!"), orig, amount, axis, rotated2, expected);

		for (; amount < 4; amount++) {
			rotated = rotated.rotate90(axis);
			rotated2 = orig.rotate(axis, amount + 1);
			System.out.format("Axis: %s, %s.equals(%s)? %b%n", axis, rotated, rotated2, rotated.equals(rotated2));
		}
		expected = orig;
		System.out.format("%s - rotated %s by 360 on %s deg to %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), axis, orig, rotated);

		System.out.println();

		axis = Rotation3D.Axis.Z;
		p = orig;
		rotated = p.rotate90(axis);
		expected = new Point3D(-2, 1, 3);
		System.out.format("%s - rotated %s by 1 on %s to %s, expected %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), p, axis, rotated, expected);
		amount = 1;
		rotated2 = orig.rotate(axis, amount);
		System.out.format("%s - rotated %s by %d on %s to %s, expected %s%n",
				(rotated2.equals(expected) ? "Correct!" : "Error!"), orig, amount, axis, rotated2, expected);

		for (; amount < 4; amount++) {
			rotated = rotated.rotate90(axis);
			rotated2 = orig.rotate(axis, amount + 1);
			System.out.format("Axis: %s, %s.equals(%s)? %b%n", axis, rotated, rotated2, rotated.equals(rotated2));
		}
		expected = orig;
		System.out.format("%s - rotated %s by %d on %s to %s, expected %s%n",
				(rotated.equals(expected) ? "Correct!" : "Error!"), orig, amount, axis, rotated2, expected);
	}
}
