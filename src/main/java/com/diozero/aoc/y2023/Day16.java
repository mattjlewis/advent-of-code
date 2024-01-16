package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day16 extends Day {
	private static final char MIRROR_LEFT_RIGHT_CHAR = '/';
	private static final char MIRROR_RIGHT_LEFT_CHAR = '\\';
	private static final char SPLITTER_HORIZONTAL_CHAR = '-';
	private static final char SPLITTER_VERTICAL_CHAR = '|';

	public static void main(String[] args) {
		new Day16().run();
	}

	@Override
	public String name() {
		return "The Floor Will Be Lava";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(load(input).getEnergisedTileCount(new Beam(Point2D.ORIGIN, CompassDirection.EAST)));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final DeflectorGrid grid = load(input);

		return Integer
				.toString(
						Stream.concat(
								Stream.concat(
										IntStream.range(0, grid.width)
												.mapToObj(x -> new Beam(new Point2D(x, 0), CompassDirection.NORTH)),
										IntStream.range(0, grid.width)
												.mapToObj(x -> new Beam(new Point2D(x, grid.height - 1),
														CompassDirection.SOUTH))),
								Stream.concat(
										IntStream.range(0, grid.height)
												.mapToObj(y -> new Beam(new Point2D(0, y), CompassDirection.EAST)),
										IntStream.range(0, grid.width).mapToObj(
												y -> new Beam(new Point2D(grid.width - 1, y), CompassDirection.WEST))))
								.parallel().mapToInt(grid::getEnergisedTileCount).max().orElseThrow());
	}

	private static DeflectorGrid load(Path input) throws IOException {
		final char[][] grid = TextParser.loadCharMatrix(input);
		final Map<Point2D, Deflector> deflectors = new HashMap<>();

		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[y].length; x++) {
				if (grid[y][x] != TextParser.UNSET_CHAR) {
					deflectors.put(new Point2D(x, y), Deflector.of(grid[y][x]));
				}
			}
		}

		return new DeflectorGrid(grid[0].length, grid.length, deflectors);
	}

	private static enum Deflector {
		MIRROR_BOTTOM_LEFT_TOP_RIGHT(false), MIRROR_TOP_LEFT_BOTTOM_RIGHT(false), SPLITTER_HORIZONTAL(true),
		SPLITTER_VERTICAL(true);

		private boolean splitter;

		private Deflector(boolean splitter) {
			this.splitter = splitter;
		}

		public Stream<CompassDirection> next(CompassDirection direction) {
			if (splitter) {
				if (direction.isHorizontal() && this == SPLITTER_HORIZONTAL
						|| direction.isVertical() && this == SPLITTER_VERTICAL) {
					return Stream.of(direction);
				}
				return Stream.of(direction.turnLeft90(), direction.turnRight90());
			}

			if (this == MIRROR_BOTTOM_LEFT_TOP_RIGHT) {
				return Stream.of(direction.isHorizontal() ? direction.turnLeft90() : direction.turnRight90());
			}
			return Stream.of(direction.isHorizontal() ? direction.turnRight90() : direction.turnLeft90());
		}

		public static Deflector of(char ch) {
			return switch (ch) {
			case MIRROR_LEFT_RIGHT_CHAR -> MIRROR_BOTTOM_LEFT_TOP_RIGHT;
			case MIRROR_RIGHT_LEFT_CHAR -> MIRROR_TOP_LEFT_BOTTOM_RIGHT;
			case SPLITTER_HORIZONTAL_CHAR -> SPLITTER_HORIZONTAL;
			case SPLITTER_VERTICAL_CHAR -> SPLITTER_VERTICAL;
			default -> throw new IllegalArgumentException("Invalid value '" + ch + "'");
			};
		}
	}

	private static record DeflectorGrid(int width, int height, Map<Point2D, Deflector> deflectors) {
		public Stream<Beam> next(final Beam beam) {
			final Deflector deflector = deflectors.get(beam.location);
			if (deflector == null) {
				final Point2D next_location = beam.location.move(beam.direction);
				if (!next_location.inBounds(0, 0, width, height)) {
					return Stream.empty();
				}
				return Stream.of(new Beam(next_location, beam.direction));
			}

			return deflector.next(beam.direction).map(dir -> new Beam(beam.location.move(dir), dir))
					.filter(b -> b.location.inBounds(0, 0, width, height));
		}

		public int getEnergisedTileCount(final Beam start) {
			final Set<Beam> energised_beams = new HashSet<>();
			final Queue<Beam> queue = new LinkedList<>();
			queue.add(start);

			while (!queue.isEmpty()) {
				final Beam beam = queue.remove();
				energised_beams.add(beam);

				next(beam).filter(next -> !energised_beams.contains(next)).forEach(queue::add);
			}

			return energised_beams.stream().map(Beam::location).collect(Collectors.toSet()).size();
		}
	}

	private static record Beam(Point2D location, CompassDirection direction) {
	}
}
