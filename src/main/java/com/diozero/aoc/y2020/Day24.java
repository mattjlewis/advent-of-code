package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.SetUtil;

public class Day24 extends Day {
	private static final Pattern PATTERN = Pattern.compile("(se|sw|ne|nw|e|w)");

	public static void main(String[] args) {
		new Day24().run();
	}

	@Override
	public String name() {
		return "Lobby Layout";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(loadData(input).size());
	}

	@Override
	public String part2(final Path input) throws IOException {
		Set<Point2D> black_tiles = loadData(input);

		final Set<Point2D> checked_white_tiles = new HashSet<>();
		for (int day = 1; day <= 100; day++) {
			final Set<Point2D> new_black_tiles = new HashSet<>();

			for (Point2D tile : black_tiles) {
				final Set<Point2D> adjacent_tile_positions = HexagonalDirection.getAdjacentPositions(tile);

				final long black_adjacent_black = SetUtil.intersectionCount(adjacent_tile_positions, black_tiles);
				if (black_adjacent_black != 0 && black_adjacent_black <= 2) {
					/*
					 * Any black tile with zero or more than 2 black tiles immediately adjacent to
					 * it is flipped to white.
					 */
					new_black_tiles.add(tile);
				}

				// Process the white tiles - white tiles is an infinite set of those not
				// contained in the list of black tiles
				for (Point2D adjacent_tile : adjacent_tile_positions) {
					if (black_tiles.contains(adjacent_tile) || checked_white_tiles.contains(adjacent_tile)) {
						continue;
					}

					// Count the number of black tiles next to this white tile
					if (SetUtil.intersectionCount(HexagonalDirection.getAdjacentPositions(adjacent_tile),
							black_tiles) == 2) {
						/*
						 * Any white tile with exactly 2 black tiles immediately adjacent to it is
						 * flipped to black.
						 */
						new_black_tiles.add(adjacent_tile);
					}

					// Prevent checking a white tile multiple times
					checked_white_tiles.add(adjacent_tile);
				}
			}

			black_tiles = new_black_tiles;
			checked_white_tiles.clear();

			Logger.debug("Day {}: {}", day, black_tiles.size());
		}

		return Long.toString(black_tiles.size());
	}

	private static Set<Point2D> loadData(final Path input) throws IOException {
		final Set<Point2D> black_tiles = new HashSet<>();
		Files.lines(input).map(Day24::parseDeltas).map(Point2D::sum)
				.forEach(tile -> SetUtil.addOrRemove(black_tiles, tile));
		return black_tiles;
	}

	private static List<Point2D> parseDeltas(final String line) {
		return PATTERN.matcher(line).results().map(mr -> HexagonalDirection.valueOf(mr.group(1)).delta).toList();
	}

	private enum HexagonalDirection {
		e(2, 0), se(1, -1), sw(-1, -1), w(-2, 0), nw(-1, 1), ne(1, 1);

		static final Set<Point2D> ADJACENT_POSITIONS;
		static {
			ADJACENT_POSITIONS = Arrays.stream(HexagonalDirection.values()).map(HexagonalDirection::delta)
					.collect(Collectors.toUnmodifiableSet());
		}

		public static Set<Point2D> getAdjacentPositions(Point2D hexagon) {
			return ADJACENT_POSITIONS.stream().map(hexagon::translate).collect(Collectors.toSet());
		}

		private Point2D delta;

		HexagonalDirection(int dx, int dy) {
			this.delta = new Point2D(dx, dy);
		}

		public Point2D delta() {
			return delta;
		}
	}
}
