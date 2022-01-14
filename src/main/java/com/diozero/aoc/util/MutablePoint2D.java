package com.diozero.aoc.util;

import java.util.List;

public class MutablePoint2D {
	private int x;
	private int y;

	public MutablePoint2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public void translate(Point2D delta) {
		this.x += delta.x();
		this.y += delta.y();
	}

	public MutablePoint2D translate(List<Point2D> deltas) {
		deltas.forEach(this::translate);
		return this;
	}

	public Point2D toPoint2D() {
		return new Point2D(x, y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		MutablePoint2D other = (MutablePoint2D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
