package com.diozero.aoc.geometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

		return create(x1, y1, x2, y2);
	}

	public static Line2D create(Point2D p1, Point2D p2) {
		return create(p1.x(), p1.y(), p2.x(), p2.y());
	}

	public static Line2D create(int x1, int y1, int x2, int y2) {
		Direction direction;
		if (x1 != x2 && y1 == y2) {
			direction = Direction.HORIZONTAL;
		} else if (x1 == x2 && y1 != y2) {
			direction = Direction.VERTICAL;
		} else {
			direction = Direction.DIAGONAL;
		}

		return new Line2D(x1, y1, x2, y2, direction);
	}

	public int minX() {
		return Math.min(x1, x2);
	}

	public int maxX() {
		return Math.max(x1, x2);
	}

	public int minY() {
		return Math.min(y1, y2);
	}

	public int maxY() {
		return Math.max(y1, y2);
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

	public CompassDirection compassDirection() {
		return switch (direction) {
		case HORIZONTAL -> goesForwards() ? CompassDirection.EAST : CompassDirection.WEST;
		case VERTICAL -> goesUp() ? CompassDirection.SOUTH : CompassDirection.NORTH;
		case DIAGONAL -> goesUp() ? (goesForwards() ? CompassDirection.SOUTH_EAST : CompassDirection.SOUTH_WEST)
				: (goesForwards() ? CompassDirection.NORTH_EAST : CompassDirection.NORTH_WEST);
		default -> throw new IllegalArgumentException();
		};
	}

	public static Set<Point2D> pathIntersections(List<Line2D> path1, List<Line2D> path2) {
		return path1.stream().flatMap(l1 -> path2.stream().map(l2 -> l1.intersection(l2)).filter(Optional::isPresent))
				.map(Optional::get).dropWhile(i -> i.equals(Point2D.ORIGIN)).collect(Collectors.toSet());
	}

	public static Set<Point2D> pathIntersections(List<Line2D> path) {
		final Set<Point2D> intersections = new HashSet<>();
		for (int i = 0; i < path.size(); i++) {
			for (int j = i + 1; j < path.size(); j++) {
				path.get(i).intersection(path.get(j)).ifPresent(intersections::add);
			}
		}
		return intersections;
	}

	public Optional<Point2D> intersection(Line2D other) {
		// Assume lines are parallel if in the same direction
		if (direction == other.direction()) {
			return Optional.empty();
		}

		// No intersection? Note must actually cross and not just touch
		if (other.minX() >= maxX() || other.maxX() <= minX() || other.minY() >= maxY() || other.maxY() <= minY()) {
			return Optional.empty();
		}

		return switch (direction) {
		case DIAGONAL -> throw new IllegalArgumentException("Unsupported direction " + direction);
		// Direction of other must be VERTICAL
		case HORIZONTAL -> Optional.of(new Point2D(other.x1, y1));
		// Direction of other must be HORIZONTAL
		case VERTICAL -> Optional.of(new Point2D(x1, other.y1));
		default -> throw new IllegalArgumentException("Invalid direction " + direction);
		};
	}

	public boolean contains(Point2D p) {
		return switch (direction) {
		case DIAGONAL -> throw new IllegalArgumentException("Unsupported direction " + direction);
		case HORIZONTAL -> minX() <= p.x() && maxX() >= p.x() && y1 == p.y();
		case VERTICAL -> minY() <= p.y() && maxY() >= p.y() && x1 == p.x();
		default -> throw new IllegalArgumentException("Invalid direction " + direction);
		};
	}

	public int length() {
		return switch (direction) {
		case DIAGONAL -> throw new IllegalArgumentException("Unsupported direction " + direction);
		case HORIZONTAL -> Math.abs(x2 - x1);
		case VERTICAL -> Math.abs(y2 - y1);
		default -> throw new IllegalArgumentException("Invalid direction " + direction);
		};
	}

	@Override
	public String toString() {
		return direction + ": (" + x1 + "," + y1 + ")->(" + x2 + "," + y2 + ")";
	}

	public Collection<? extends Point2D> toSet() {
		final Set<Point2D> points = new HashSet<>();

		switch (direction) {
		case DIAGONAL:
			for (int x = minX(); x <= maxX(); x++) {
				points.add(new Point2D(x, minY() + x - minX()));
			}
			break;
		case HORIZONTAL:
			for (int x = minX(); x <= maxX(); x++) {
				points.add(new Point2D(x, y1));
			}
			break;
		case VERTICAL:
			for (int y = minY(); y <= maxY(); y++) {
				points.add(new Point2D(x1, y));
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid direction " + direction);
		}

		return points;
	}
}
