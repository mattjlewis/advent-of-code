package com.diozero.aoc.geometry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class MutablePoint2D {
	public static Rectangle getBounds(final Collection<MutablePoint2D> points) {
		return getBounds(points.stream());
	}

	public static Rectangle getBounds(final Stream<MutablePoint2D> points) {
		int min_x = Integer.MAX_VALUE;
		int max_x = Integer.MIN_VALUE;
		int min_y = Integer.MAX_VALUE;
		int max_y = Integer.MIN_VALUE;
		final Iterator<MutablePoint2D> it = points.iterator();
		while (it.hasNext()) {
			final MutablePoint2D p = it.next();
			min_x = Math.min(min_x, p.x());
			max_x = Math.max(max_x, p.x());
			min_y = Math.min(min_y, p.y());
			max_y = Math.max(max_y, p.y());
		}

		return Rectangle.create(min_x, min_y, max_x, max_y);
	}

	private int x;
	private int y;

	public MutablePoint2D(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int x() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int y() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public MutablePoint2D translate(CompassDirection direction) {
		return translate(direction.delta());
	}

	public MutablePoint2D translate(Point2D delta) {
		this.x += delta.x();
		this.y += delta.y();

		return this;
	}

	public MutablePoint2D translate(int deltaX, int deltaY) {
		this.x += deltaX;
		this.y += deltaY;

		return this;
	}

	public MutablePoint2D translate(List<Point2D> deltas) {
		deltas.forEach(this::translate);

		return this;
	}

	public Point2D delta(MutablePoint2D other) {
		return delta(other.x, other.y);
	}

	public Point2D delta(Point2D other) {
		return delta(other.x(), other.y());
	}

	public Point2D delta(int otherX, int otherY) {
		return new Point2D(otherX - x, otherY - y);
	}

	public Point2D immutable() {
		return new Point2D(x, y);
	}

	public int manhattanDistance(MutablePoint2D other) {
		return manhattanDistance(other.x(), other.y());
	}

	public int manhattanDistance(Point2D other) {
		return manhattanDistance(other.x(), other.y());
	}

	public int manhattanDistance(int otherX, int otherY) {
		return Math.abs(otherX - x) + Math.abs(otherY - y);
	}

	@Override
	public MutablePoint2D clone() {
		return new MutablePoint2D(x, y);
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
