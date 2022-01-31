package com.diozero.aoc.geometry;

public class MutablePoint3D {
	private int x, y, z;

	public MutablePoint3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void translate(Point3D delta) {
		x += delta.x();
		y += delta.y();
		z += delta.z();
	}

	public void translate(MutablePoint3D delta) {
		x += delta.x;
		y += delta.y;
		z += delta.z;
	}

	public void translate(int deltaX, int deltaY, int deltaZ) {
		x += deltaX;
		y += deltaY;
		z += deltaZ;
	}

	public Point3D immutable() {
		return new Point3D(x, y, z);
	}

	public int manhattanDistance(Point3D other) {
		return Math.abs(x - other.x()) + Math.abs(y - other.y()) + Math.abs(z - other.z());
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MutablePoint3D other = (MutablePoint3D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
}
