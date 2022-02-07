package com.diozero.aoc.geometry;

public enum CompassDirection {
	SOUTH(0, -1), SOUTH_EAST(1, -1), EAST(1, 0), NORTH_EAST(1, 1), NORTH(0, 1), NORTH_WEST(-1, 1), WEST(-1, 0),
	SOUTH_WEST(-1, -1);

	private final Point2D delta;

	CompassDirection(int dx, int dy) {
		delta = new Point2D(dx, dy);
	}

	public int dx() {
		return delta.x();
	}

	public int dy() {
		return delta.y();
	}

	public Point2D delta() {
		return delta;
	}

	public CompassDirection opposite() {
		return values()[(ordinal() + values().length / 2) % values().length];
		/*-
		return switch (this) {
		case SOUTH -> NORTH;
		case SOUTH_EAST -> NORTH_WEST;
		case EAST -> WEST;
		case NORTH_EAST -> SOUTH_WEST;
		case NORTH -> SOUTH;
		case NORTH_WEST -> SOUTH_EAST;
		case WEST -> EAST;
		case SOUTH_WEST -> NORTH_EAST;
		default -> throw new IllegalArgumentException();
		};
		*/
	}

	public CompassDirection turnLeft90() {
		return values()[(ordinal() - 2 + values().length) % values().length];
	}

	public CompassDirection turnRight90() {
		return values()[(ordinal() + 2) % values().length];
	}
}
