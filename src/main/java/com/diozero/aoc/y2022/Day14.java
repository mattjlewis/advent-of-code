package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Line2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.PrintUtil;

public class Day14 extends Day {
	private static final Pattern ROCK_PATH_PATTERN = Pattern.compile("(\\d+),(\\d+)");

	private static final Point2D SAND_SOURCE = new Point2D(500, 0);

	private static final CompassDirection DOWN = CompassDirection.NORTH;
	private static final CompassDirection DOWN_LEFT = CompassDirection.NORTH_WEST;
	private static final CompassDirection DOWN_RIGHT = CompassDirection.NORTH_EAST;

	public static void main(String[] args) {
		new Day14().run();
	}

	@Override
	public String name() {
		return "Regolith Reservoir";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(solve(Cave.create(Files.lines(input).map(Day14::parse).toList(), true)));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(solve(Cave.create(Files.lines(input).map(Day14::parse).toList(), false)));
	}

	private static int solve(Cave cave) {
		if (Logger.isDebugEnabled()) {
			PrintUtil.print(cave.contents, PrintUtil.BLANK_PIXEL, Cave::toChar);
		}

		boolean complete = false;
		int sand_units = 0;
		while (!complete) {
			Point2D sand_pos = SAND_SOURCE;
			sand_units++;
			while (true) {
				if (Logger.isTraceEnabled()) {
					PrintUtil.print(cave.contents, PrintUtil.BLANK_PIXEL, Cave::toChar);
				}

				if (cave.inFreefall(sand_pos)) {
					complete = true;
					// This sand unit doesn't "land" so decrement the counter
					sand_units--;
					break;
				}

				Point2D p = sand_pos.translate(DOWN);

				// Have we reached floor level? (part 2 only)
				if (cave.hasReachedFloor(p)) {
					// Position the sand where it was and break out of the loop
					cave.sand(sand_pos);
					break;
				}

				// Can the unit of sand move directly down?
				if (!cave.contains(p)) {
					// ... move down one and continue
					sand_pos = p;
					continue;
				}

				// ... else is there space down and left?
				p = sand_pos.translate(DOWN_LEFT);
				if (!cave.contains(p)) {
					sand_pos = p;
					continue;
				}

				// ... else is there space down and right?
				p = sand_pos.translate(DOWN_RIGHT);
				if (!cave.contains(p)) {
					sand_pos = p;
					continue;
				}

				// ... else leave it where it is
				cave.sand(sand_pos);
				// Part 2 is complete when the sand can no longer move
				if (sand_pos.equals(SAND_SOURCE)) {
					complete = true;
				}
				break;
			}
		}
		if (Logger.isDebugEnabled()) {
			PrintUtil.print(cave.contents, PrintUtil.BLANK_PIXEL, Cave::toChar);
		}

		return sand_units;
	}

	private static List<Point2D> parse(String line) {
		return ROCK_PATH_PATTERN.matcher(line).results()
				.map(mr -> new Point2D(Integer.parseInt(mr.group(1)), Integer.parseInt(mr.group(2)))).toList();
	}

	private static record Cave(Map<Point2D, Character> contents, int lowestRock, Optional<Integer> floorLevel) {

		private static final Character ROCK = Character.valueOf('#');
		private static final Character SAND = Character.valueOf('o');

		public static Cave create(final List<List<Point2D>> wallStartAndEnds, boolean part1) {
			final Map<Point2D, Character> contents = new HashMap<>();

			int lowest_rock = Integer.MIN_VALUE;

			for (final List<Point2D> wall : wallStartAndEnds) {
				Point2D start = null;
				for (final Point2D start_or_end : wall) {
					lowest_rock = Math.max(lowest_rock, start_or_end.y());

					if (start == null) {
						start = start_or_end;
						continue;
					}

					Line2D.create(start, start_or_end).toSet().forEach(p -> contents.put(p, ROCK));

					start = start_or_end;
				}
			}

			return new Cave(contents, lowest_rock,
					part1 ? Optional.empty() : Optional.of(Integer.valueOf(lowest_rock + 2)));
		}

		public static char toChar(Character ch) {
			return switch (ch.charValue()) {
			case '#' -> PrintUtil.FILLED_PIXEL;
			case 'o' -> SAND.charValue();
			default -> PrintUtil.BLANK_PIXEL;
			};
		}

		public boolean contains(final Point2D p) {
			return contents.containsKey(p);
		}

		public void sand(final Point2D p) {
			contents.put(p, SAND);
		}

		public boolean inFreefall(Point2D sandPos) {
			return floorLevel.isEmpty() && sandPos.y() >= lowestRock;
			// return floorLevel == null && sandPos.y() >= lowestRock;
		}

		public boolean hasReachedFloor(Point2D p) {
			return floorLevel.isPresent() && floorLevel.get().intValue() == p.y();
			// return floorLevel != null && p.y() == floorLevel.intValue();
		}
	}
}
