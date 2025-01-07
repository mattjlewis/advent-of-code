package com.diozero.aoc.geometry;

import java.util.List;

public enum CompassDirection {
	SOUTH(0, -1), SOUTH_EAST(1, -1), EAST(1, 0), NORTH_EAST(1, 1), NORTH(0, 1), NORTH_WEST(-1, 1), WEST(-1, 0),
	SOUTH_WEST(-1, -1);

	public static final List<CompassDirection> NESW = List.of(NORTH, EAST, SOUTH, WEST);

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

	public boolean isHorizontal() {
		return this == EAST || this == WEST;
	}

	public boolean isVertical() {
		return this == NORTH || this == SOUTH;
	}

	public static CompassDirection fromUdlr(String udlr) {
		return switch (udlr) {
		case "U" -> CompassDirection.NORTH;
		case "D" -> CompassDirection.SOUTH;
		case "L" -> CompassDirection.WEST;
		case "R" -> CompassDirection.EAST;
		default -> throw new IllegalArgumentException("Invalid UDLR value '" + udlr + "'");
		};
	}

	public static CompassDirection fromUdlrSwapped(String udlr) {
		return switch (udlr) {
		case "U" -> CompassDirection.SOUTH;
		case "D" -> CompassDirection.NORTH;
		case "L" -> CompassDirection.WEST;
		case "R" -> CompassDirection.EAST;
		default -> throw new IllegalArgumentException("Invalid UDLR value '" + udlr + "'");
		};
	}

	public static CompassDirection fromArrow(int arrow) {
		return switch ((char) arrow) {
		case '^' -> CompassDirection.SOUTH;
		case '>' -> CompassDirection.EAST;
		case '<' -> CompassDirection.WEST;
		case 'v' -> CompassDirection.NORTH;
		default -> throw new IllegalArgumentException("Invalid arrow value '" + arrow + "'");
		};
	}

	public char toArrow() {
		return switch (this) {
		case SOUTH -> '^';
		case EAST -> '>';
		case WEST -> '<';
		case NORTH -> 'v';
		default ->
			throw new IllegalArgumentException("CompassDirection '" + this + "' cannot be converted to an arrow");
		};
	}
}
