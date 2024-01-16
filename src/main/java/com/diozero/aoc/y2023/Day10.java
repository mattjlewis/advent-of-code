package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.TextParser;

public class Day10 extends Day {
	private static final char GROUND_TILE = TextParser.UNSET_CHAR;
	private static final char VERTICAL_TILE = '|';
	private static final char HORIZONTAL_TILE = '-';
	private static final char NE_BEND_TILE = 'L';
	private static final char NW_BEND_TILE = 'J';
	private static final char SW_BEND_TILE = '7';
	private static final char SE_BEND_TILE = 'F';
	private static final char OUTSIDE_TILE = 'O';

	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public String name() {
		return "Pipe Maze";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(buildPipeMaze(TextParser.loadCharMatrix(input)).length() / 2);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final PipeMaze maze = buildPipeMaze(TextParser.loadCharMatrix(input));

		// Double the size of the grid but keep the pipe size the same so that there is
		// space between adjacent pipes
		final int doubled_width = maze.width * 2 + 1;
		final int doubled_height = maze.height * 2 + 1;
		final char[][] doubled_grid = MatrixUtil.initialiseMatrix(doubled_width, doubled_height, GROUND_TILE);

		// Re-construct the pipe loop in the doubled grid (offset by 1 so we can flood
		// fill from a single corner)
		Pipe pipe = maze.start;
		do {
			doubled_grid[pipe.location.y() * 2 + 1][pipe.location.x() * 2 + 1] = pipe.type.tile;
			if (pipe.type.directions.contains(CompassDirection.EAST)) {
				doubled_grid[pipe.location.y() * 2 + 1][pipe.location.x() * 2 + 1 + 1] = HORIZONTAL_TILE;
			}
			if (pipe.type.directions.contains(CompassDirection.NORTH)) {
				doubled_grid[pipe.location.y() * 2 + 1 + 1][pipe.location.x() * 2 + 1] = VERTICAL_TILE;
			}

			pipe = maze.next(pipe);
		} while (!pipe.start);

		// PrintUtil.print(doubled_grid);

		// Flood fill from the top-left corner - the grid is expanded in all directions
		MatrixUtil.floodFill(doubled_grid, 0, 0, OUTSIDE_TILE);

		// PrintUtil.print(doubled_grid);

		// Count how many ground tiles remain on odd tiles - ignore the added tiles
		int count = 0;
		for (int y = 1; y < doubled_height; y += 2) {
			for (int x = 1; x < doubled_width; x += 2) {
				if (doubled_grid[y][x] == GROUND_TILE) {
					count++;
				}
			}
		}

		return Integer.toString(count);
	}

	private static PipeMaze buildPipeMaze(char[][] grid) {
		final Point2D start_pos = MatrixUtil.find(grid, 'S').orElseThrow();
		final Pipe start_pipe = Pipe.create(grid, start_pos, null);

		final Map<Point2D, Pipe> pipes = new HashMap<>();
		pipes.put(start_pos, start_pipe);

		// Build the pipe loop all the way back to the start
		Pipe pipe = start_pipe;
		do {
			final Point2D this_location = pipe.location;
			pipe = pipes.computeIfAbsent(pipe.nextLocation, location -> Pipe.create(grid, location, this_location));
		} while (!pipe.start);

		return new PipeMaze(grid[0].length, grid.length, pipes, start_pipe);
	}

	private static record PipeMaze(int width, int height, Map<Point2D, Pipe> pipes, Pipe start) {
		public Pipe next(Pipe pipe) {
			return pipes.get(pipe.nextLocation);
		}

		public int length() {
			return pipes.keySet().size();
		}
	}

	private static record Pipe(boolean start, PipeType type, Point2D location, Point2D previousLocation,
			Point2D nextLocation) {

		public static Pipe create(char[][] grid, Point2D location, Point2D previousLocation) {
			boolean start;
			PipeType type;
			Point2D previous_location;
			Point2D next_location;

			if (grid[location.y()][location.x()] == 'S') {
				start = true;
				// Populate the previous and next locations
				// Find the two pipes that connect to the start location
				final List<CompassDirection> dirs = CompassDirection.NESW.stream()
						.filter(dir -> navigatable(grid, location, dir)).toList();
				if (dirs.size() != 2) {
					throw new IllegalStateException("Expected 2 possible directions, got " + dirs.size());
				}
				if (dirs.containsAll(PipeType.VERTICAL.directions)) {
					type = PipeType.VERTICAL;
				} else if (dirs.containsAll(PipeType.NE_BEND.directions)) {
					type = PipeType.NE_BEND;
				} else if (dirs.containsAll(PipeType.NW_BEND.directions)) {
					type = PipeType.NW_BEND;
				} else if (dirs.containsAll(PipeType.SW_BEND.directions)) {
					type = PipeType.SW_BEND;
				} else if (dirs.containsAll(PipeType.SE_BEND.directions)) {
					type = PipeType.SE_BEND;
				} else {
					type = PipeType.HORIZONTAL;
				}
				previous_location = location.move(dirs.getLast());
				next_location = location.move(dirs.getFirst());
			} else {
				start = false;
				type = PipeType.of(grid[location.y()][location.x()]);
				previous_location = previousLocation;
				next_location = type.directions.stream().map(location::move).filter(l -> !previousLocation.equals(l))
						.findAny().orElseThrow();
			}

			return new Pipe(start, type, location, previous_location, next_location);
		}

		public static boolean navigatable(char[][] grid, Point2D location, CompassDirection direction) {
			final Point2D neighbour = location.move(direction);
			if (!neighbour.inBounds(0, 0, grid[0].length, grid.length)) {
				return false;
			}
			if (grid[neighbour.y()][neighbour.x()] == GROUND_TILE) {
				return false;
			}
			return PipeType.of(grid[neighbour.y()][neighbour.x()]).directions.contains(direction.opposite());
		}
	}

	/*-
	 * | is a vertical pipe connecting north and south.
	 * - is a horizontal pipe connecting east and west.
	 * L is a 90-degree bend connecting north and east.
	 * J is a 90-degree bend connecting north and west.
	 * 7 is a 90-degree bend connecting south and west.
	 * F is a 90-degree bend connecting south and east.
	 * . is ground; there is no pipe in this tile.
	 * S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
	 */
	private static enum PipeType {
		// Note North and South are logically swapped as y delta for North is +1
		VERTICAL(Day10.VERTICAL_TILE, List.of(CompassDirection.NORTH, CompassDirection.SOUTH)),
		HORIZONTAL(Day10.HORIZONTAL_TILE, List.of(CompassDirection.EAST, CompassDirection.WEST)),
		NE_BEND(Day10.NE_BEND_TILE, List.of(CompassDirection.SOUTH, CompassDirection.EAST)),
		NW_BEND(Day10.NW_BEND_TILE, List.of(CompassDirection.SOUTH, CompassDirection.WEST)),
		SW_BEND(Day10.SW_BEND_TILE, List.of(CompassDirection.NORTH, CompassDirection.WEST)),
		SE_BEND(Day10.SE_BEND_TILE, List.of(CompassDirection.NORTH, CompassDirection.EAST));

		public static PipeType of(int value) {
			return switch ((char) value) {
			case Day10.VERTICAL_TILE -> VERTICAL;
			case Day10.HORIZONTAL_TILE -> HORIZONTAL;
			case Day10.NE_BEND_TILE -> NE_BEND;
			case Day10.NW_BEND_TILE -> NW_BEND;
			case Day10.SW_BEND_TILE -> SW_BEND;
			case Day10.SE_BEND_TILE -> SE_BEND;
			default -> throw new IllegalArgumentException("Invalid value '" + ((char) value) + "'");
			};
		}

		private char tile;
		private List<CompassDirection> directions;

		private PipeType(char tile, List<CompassDirection> directions) {
			this.tile = tile;
			this.directions = directions;
		}

		@Override
		public String toString() {
			return Character.toString(tile);
		}
	}
}
