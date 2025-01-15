package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Rectangle;
import com.diozero.aoc.util.Tuple2;

public class Day22 extends Day {
	private static final Pattern MOVEMENT_PATTERN = Pattern.compile("(\\d+)([LR])?");

	// 0 = top, 1 = back, 2 = left, 3 = front, 4 = bottom, 5 = right
	private static enum Face {
		TOP, BACK, LEFT, FRONT, BOTTOM, RIGHT;
	}

	public static void main(String[] args) {
		new Day22().run();
	}

	@Override
	public String name() {
		return "Monkey Map";
	}

	@Override
	public String part1(Path input) throws IOException {
		final String[] parts = Files.readString(input).split("\\n\\n");

		final String[] lines = parts[0].split("\n");
		final int width = Arrays.stream(lines).mapToInt(String::length).max().orElseThrow();
		final int height = lines.length;
		final char[][] grid = new char[lines.length][];
		for (int y = 0; y < lines.length; y++) {
			grid[y] = lines[y].toCharArray();
		}

		final List<Movement> movements = MOVEMENT_PATTERN.matcher(parts[1]).results().map(Movement::create).toList();

		// You begin the path in the leftmost open tile of the top row of tiles
		final int start_x = IntStream.range(0, lines[0].length()).takeWhile(x -> lines[0].charAt(x) == ' ').max()
				.orElseThrow();
		Point2D pos = new Point2D(start_x, 0);
		CompassDirection direction = CompassDirection.EAST;

		for (Movement movement : movements) {
			for (int i = 0; i < movement.distance; i++) {
				Point2D next = pos.move(direction).wrap(grid[pos.y()].length, height);
				// Wrap around edges
				while (next.x() >= grid[next.y()].length || grid[next.y()][next.x()] == ' ') {
					next = next.move(direction).wrap(width, height);
				}
				if (grid[next.y()][next.x()] == '#') {
					break;
				}
				pos = next;
			}
			if (movement.turn.isPresent()) {
				direction = direction.turn(movement.turn.get());
			}
		}

		return Integer.toString((pos.y() + 1) * 1000 + (pos.x() + 1) * 4 + facing(direction));
	}

	@Override
	public String part2(Path input) throws IOException {
		final String[] parts = Files.readString(input).split("\\n\\n");

		final String[] lines = parts[0].split("\n");
		final char[][] grid = new char[lines.length][];
		for (int y = 0; y < lines.length; y++) {
			grid[y] = lines[y].toCharArray();
		}

		final List<Movement> movements = MOVEMENT_PATTERN.matcher(parts[1]).results().map(Movement::create).toList();

		final int[][] face_positions;
		if (isSample()) {
			/*-
			 * 0 = top, 1 = back, 2 = left, 3 = front, 4 = bottom, 5 = right
			 * Hard-coded based on provided data:
			 *   0
			 * 123
			 *   45
			 */
			face_positions = new int[][] { { 2, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 }, { 2, 2 }, { 3, 2 } };
		} else {
			/*-
			 * 0 = top, 1 = back, 2 = left, 3 = front, 4 = bottom, 5 = right
			 * Hard-coded based on provided data:
			 *  05
			 *  3
			 * 24
			 * 1
			 */
			face_positions = new int[][] { { 1, 0 }, { 0, 3 }, { 0, 2 }, { 1, 1 }, { 1, 2 }, { 2, 0 } };
		}
		final int width = Arrays.stream(lines).mapToInt(String::length).max().orElseThrow();
		final int height = lines.length;
		final int face_width = width > height ? width / 4 : width / 3;
		final int face_height = width > height ? height / 3 : height / 4;
		final Rectangle[] faces = Arrays.stream(face_positions).map(pos -> Rectangle.create(pos[0] * face_width,
				pos[1] * face_height, (pos[0] + 1) * face_width - 1, (pos[1] + 1) * face_height - 1))
				.toArray(Rectangle[]::new);

		// You begin the path in the leftmost open tile of the top row of tiles
		final int start_x = IntStream.range(0, lines[0].length()).takeWhile(x -> lines[0].charAt(x) == ' ').max()
				.orElseThrow();
		Point2D pos = new Point2D(start_x, 0);
		CompassDirection direction = CompassDirection.EAST;

		for (Movement movement : movements) {
			for (int i = 0; i < movement.distance; i++) {
				Point2D next_pos = pos.move(direction);
				CompassDirection next_dir = direction;
				// Wrap around edges of the cube
				if (next_pos.y() < 0 || next_pos.y() >= grid.length || next_pos.x() < 0
						|| next_pos.x() >= grid[next_pos.y()].length || grid[next_pos.y()][next_pos.x()] == ' ') {
					final Tuple2<Point2D, CompassDirection> warped = warp(pos, direction, faces);
					next_pos = warped.first();
					next_dir = warped.second();
				}
				if (grid[next_pos.y()][next_pos.x()] == '#') {
					break;
				}
				pos = next_pos;
				direction = next_dir;
			}
			if (movement.turn.isPresent()) {
				direction = direction.turn(movement.turn.get());
			}
		}

		return Integer.toString((pos.y() + 1) * 1000 + (pos.x() + 1) * 4 + facing(direction));
	}

	private Tuple2<Point2D, CompassDirection> warp(Point2D pos, CompassDirection direction, Rectangle[] faces) {
		if (isSample()) {
			return warpSample(pos, direction, faces);
		}

		return warpNotSample(pos, direction, faces);
	}

	@SuppressWarnings("incomplete-switch")
	private static Tuple2<Point2D, CompassDirection> warpSample(Point2D pos, CompassDirection direction,
			Rectangle[] faces) {
		final Face cur_face = getFace(pos, faces);
		final Rectangle cur_face_bounds = faces[cur_face.ordinal()];

		Point2D next_pos = null;
		CompassDirection next_dir = null;
		Rectangle next_face_bounds;
		switch (cur_face) {
		case TOP:
			switch (direction) {
			case NORTH:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case SOUTH:
				next_pos = new Point2D(pos.x(), faces[Face.BOTTOM.ordinal()].y2());
				next_dir = direction;
				break;
			case EAST:
				next_face_bounds = faces[Face.RIGHT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2(), next_face_bounds.y2() - pos.y());
				next_dir = CompassDirection.WEST;
				break;
			case WEST:
				next_face_bounds = faces[Face.LEFT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + pos.y(), next_face_bounds.y1());
				next_dir = CompassDirection.NORTH;
				break;
			}
			break;
		case BOTTOM:
			switch (direction) {
			case NORTH:
				next_face_bounds = faces[Face.BACK.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2() - (pos.x() - cur_face_bounds.x1()), next_face_bounds.y2());
				next_dir = CompassDirection.SOUTH;
				break;
			case SOUTH, EAST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case WEST:
				next_face_bounds = faces[Face.LEFT.ordinal()];
				next_pos = new Point2D(next_face_bounds.y2(), next_face_bounds.x1() + pos.y());
				next_dir = CompassDirection.SOUTH;
				break;
			}
			break;
		case FRONT:
			switch (direction) {
			case NORTH, SOUTH, WEST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case EAST:
				next_face_bounds = faces[Face.RIGHT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + (cur_face_bounds.y2() - pos.y()), next_face_bounds.y1());
				next_dir = CompassDirection.NORTH;
				break;
			}
			break;
		case LEFT:
			switch (direction) {
			case EAST, WEST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case NORTH:
				next_face_bounds = faces[Face.BOTTOM.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1(), next_face_bounds.y2() - (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.EAST;
				break;
			case SOUTH:
				next_face_bounds = faces[Face.TOP.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1(), next_face_bounds.y1() + (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.EAST;
				break;
			}
			break;
		case RIGHT:
			switch (direction) {
			case WEST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case SOUTH:
				next_face_bounds = faces[Face.FRONT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2(), next_face_bounds.y2() - (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.WEST;
				break;
			case NORTH:
				next_face_bounds = faces[Face.BACK.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1(), next_face_bounds.y2() - (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.EAST;
				break;
			case EAST:
				next_face_bounds = faces[Face.TOP.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2(), next_face_bounds.y2() - (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.WEST;
				break;
			}
			break;
		case BACK:
			switch (direction) {
			// XXX Not actually used
			}
			break;
		}

		return new Tuple2<>(next_pos, next_dir);
	}

	@SuppressWarnings("incomplete-switch")
	private static Tuple2<Point2D, CompassDirection> warpNotSample(Point2D pos, CompassDirection direction,
			Rectangle[] faces) {
		final Face cur_face = getFace(pos, faces);
		final Rectangle cur_face_bounds = faces[cur_face.ordinal()];

		Point2D next_pos = null;
		CompassDirection next_dir = null;
		Rectangle next_face_bounds;
		switch (cur_face) {
		case TOP:
			switch (direction) {
			case NORTH, EAST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case SOUTH:
				next_face_bounds = faces[Face.BACK.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1(), next_face_bounds.y1() + (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.EAST;
				break;
			case WEST:
				next_face_bounds = faces[Face.LEFT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1(), next_face_bounds.y2() - (pos.y() - cur_face_bounds.y1()));
				next_dir = CompassDirection.EAST;
				break;
			}
			break;
		case BOTTOM:
			switch (direction) {
			case SOUTH, WEST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case NORTH:
				next_face_bounds = faces[Face.BACK.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2(), next_face_bounds.y1() + (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.WEST;
				break;
			case EAST:
				next_face_bounds = faces[Face.RIGHT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2(), next_face_bounds.y2() - (pos.y() - cur_face_bounds.y1()));
				next_dir = CompassDirection.WEST;
				break;
			}
			break;
		case FRONT:
			switch (direction) {
			case NORTH, SOUTH:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case EAST:
				next_face_bounds = faces[Face.RIGHT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + (pos.y() - cur_face_bounds.y1()), next_face_bounds.y2());
				next_dir = CompassDirection.SOUTH;
				break;
			case WEST:
				next_face_bounds = faces[Face.LEFT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + (pos.y() - cur_face_bounds.y1()), next_face_bounds.y1());
				next_dir = CompassDirection.NORTH;
				break;
			}
			break;
		case LEFT:
			switch (direction) {
			case NORTH, EAST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case WEST:
				next_face_bounds = faces[Face.TOP.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1(), next_face_bounds.y2() - (pos.y() - cur_face_bounds.y1()));
				next_dir = CompassDirection.EAST;
				break;
			case SOUTH:
				next_face_bounds = faces[Face.FRONT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1(), next_face_bounds.y1() + (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.EAST;
				break;
			}
			break;
		case RIGHT:
			switch (direction) {
			case WEST:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case NORTH:
				next_face_bounds = faces[Face.FRONT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2(), next_face_bounds.y1() + (pos.x() - cur_face_bounds.x1()));
				next_dir = CompassDirection.WEST;
				break;
			case SOUTH:
				next_face_bounds = faces[Face.BACK.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + (pos.x() - cur_face_bounds.x1()), next_face_bounds.y2());
				next_dir = CompassDirection.SOUTH;
				break;
			case EAST:
				next_face_bounds = faces[Face.BOTTOM.ordinal()];
				next_pos = new Point2D(next_face_bounds.x2(), next_face_bounds.y2() - (pos.y() - cur_face_bounds.y1()));
				next_dir = CompassDirection.WEST;
				break;
			}
			break;
		case BACK:
			switch (direction) {
			case SOUTH:
				next_pos = pos.move(direction);
				next_dir = direction;
				break;
			case NORTH:
				next_face_bounds = faces[Face.RIGHT.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + (pos.x() - cur_face_bounds.x1()), next_face_bounds.y1());
				next_dir = CompassDirection.NORTH;
				break;
			case EAST:
				next_face_bounds = faces[Face.BOTTOM.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + (pos.y() - cur_face_bounds.y1()), next_face_bounds.y2());
				next_dir = CompassDirection.SOUTH;
				break;
			case WEST:
				next_face_bounds = faces[Face.TOP.ordinal()];
				next_pos = new Point2D(next_face_bounds.x1() + (pos.y() - cur_face_bounds.y1()), next_face_bounds.y1());
				next_dir = CompassDirection.NORTH;
				break;
			}
			break;
		}

		return new Tuple2<>(next_pos, next_dir);
	}

	private static Face getFace(Point2D pos, Rectangle[] faces) {
		return Face.values()[IntStream.range(0, faces.length).filter(i -> faces[i].contains(pos)).findFirst()
				.orElseThrow()];
	}

	private static int facing(CompassDirection direction) {
		return switch (direction) {
		case EAST -> 0;
		case NORTH -> 1;
		case WEST -> 2;
		case SOUTH -> 3;
		default -> throw new IllegalArgumentException("Unexpected value: " + direction);
		};
	}

	private static record Movement(int distance, Optional<CompassDirection.Turn> turn) {
		public static Movement create(MatchResult mr) {
			return new Movement(Integer.parseInt(mr.group(1)),
					mr.group(2) == null ? Optional.empty() : Optional.of(CompassDirection.Turn.of(mr.group(2))));
		}
	}
}
