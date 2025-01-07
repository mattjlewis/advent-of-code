package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Set;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;
import com.diozero.aoc.util.Tuple2;

public class Day6 extends Day {
	public static void main(String[] args) {
		new Day6().run();
	}

	@Override
	public String name() {
		return "Guard Gallivant";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Puzzle puzzle = Puzzle.load(input);

		return Integer.toString(getPositions(puzzle).size());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Puzzle puzzle = Puzzle.load(input);

		final Set<Point2D> positions = getPositions(puzzle);

		int num_loops = 0;
		for (Point2D obstruction : positions) {
			Point2D guard_pos = puzzle.guard_pos();
			CompassDirection guard_dir = puzzle.guard_dir();

			final Set<Tuple2<Point2D, CompassDirection>> path = new HashSet<>();
			while (true) {
				final Tuple2<Point2D, CompassDirection> next_path = new Tuple2<>(guard_pos, guard_dir);
				if (path.contains(next_path)) {
					num_loops++;
					break;
				}

				path.add(next_path);

				// Is there an obstruction ahead?
				final Point2D next_guard_pos = guard_pos.move(guard_dir);
				if (puzzle.obstructions.contains(next_guard_pos) || next_guard_pos.equals(obstruction)) {
					guard_dir = guard_dir.turnRight90();
					continue;
				}

				// Out of bounds?
				if (next_guard_pos.x() < 0 || next_guard_pos.x() >= puzzle.width || //
						next_guard_pos.y() < 0 || next_guard_pos.y() >= puzzle.height) {
					break;
				}

				guard_pos = next_guard_pos;
			}
		}

		return Integer.toString(num_loops);
	}

	private static Set<Point2D> getPositions(Puzzle puzzle) {
		Point2D guard_pos = puzzle.guard_pos();
		CompassDirection guard_dir = puzzle.guard_dir();

		final Set<Point2D> positions = new HashSet<>();
		while (true) {
			positions.add(guard_pos);

			// Is there an obstruction ahead?
			final Point2D next_guard_pos = guard_pos.move(guard_dir);
			if (puzzle.obstructions.contains(next_guard_pos)) {
				guard_dir = guard_dir.turnRight90();
				continue;
			}

			// Out of bounds?
			if (next_guard_pos.x() < 0 || next_guard_pos.x() >= puzzle.width || //
					next_guard_pos.y() < 0 || next_guard_pos.y() >= puzzle.height) {
				break;
			}

			guard_pos = next_guard_pos;
		}

		return positions;
	}

	private static record Puzzle(Set<Point2D> obstructions, Point2D guard_pos, CompassDirection guard_dir, int width,
			int height) {
		static Puzzle load(Path input) throws IOException {
			final Set<Point2D> obstructions = new HashSet<>();
			Point2D guard_pos = null;
			CompassDirection guard_dir = null;

			final Iterator<String> it = Files.lines(input).iterator();
			int y;
			int x = 0;
			for (y = 0; it.hasNext(); y++) {
				PrimitiveIterator.OfInt char_it = it.next().chars().iterator();
				for (x = 0; char_it.hasNext(); x++) {
					final int ch = char_it.nextInt();
					if (ch == TextParser.SET_CHAR) {
						obstructions.add(new Point2D(x, y));
					} else if (ch == '^') {
						guard_pos = new Point2D(x, y);
						guard_dir = CompassDirection.SOUTH;
					} else if (ch != TextParser.UNSET_CHAR) {
						throw new IllegalStateException("Unexpected character '" + ((char) ch) + "'");
					}
				}
			}
			if (guard_pos == null || guard_dir == null) {
				throw new IllegalStateException("Unable to locate guard");
			}

			return new Puzzle(obstructions, guard_pos, guard_dir, x, y);
		}
	}
}
