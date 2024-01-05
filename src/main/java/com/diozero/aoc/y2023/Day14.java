package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day14 extends Day {
	private static final char ROCK = 'O';
	private static final char WALL = TextParser.SET_CHAR;
	private static final CompassDirection[] NWSE = { CompassDirection.NORTH, CompassDirection.WEST,
			CompassDirection.SOUTH, CompassDirection.EAST };

	public static void main(String[] args) {
		new Day14().run();
	}

	@Override
	public String name() {
		return "Parabolic Reflector Dish";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(ReflectorDish.parse(input).tilt().calculateLoad());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final ReflectorDish dish = ReflectorDish.parse(input);

		// Find the point at which the rock patterns start to repeat
		final List<Set<Point2D>> rock_patterns = new ArrayList<>();
		while (!rock_patterns.contains(dish.rocks)) {
			rock_patterns.add(Set.copyOf(dish.rocks));

			for (int i = 0; i < NWSE.length; i++) {
				dish.tilt().rotate();
			}
		}

		final int total_cycles = 1_000_000_000;
		final int repeat_start = rock_patterns.indexOf(dish.rocks);
		final int repeat_size = rock_patterns.size() - repeat_start;

		return Integer.toString(ReflectorDish.calculateLoad(
				rock_patterns.get(repeat_start + (total_cycles - repeat_start) % repeat_size), dish.size));
	}

	private static record ReflectorDish(int size, EnumMap<CompassDirection, Set<Point2D>> walls, Set<Point2D> rocks,
			AtomicInteger directionIndex) {
		public static ReflectorDish parse(Path input) throws IOException {
			final char[][] data = TextParser.loadCharMatrix(input);
			// Has to be square for the rotations
			final int size = data.length;

			final Set<Point2D> north_walls = new HashSet<>();
			final Set<Point2D> rocks = new HashSet<>();
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++) {
					switch (data[y][x]) {
					case WALL:
						north_walls.add(new Point2D(x, y));
						break;
					case ROCK:
						rocks.add(new Point2D(x, y));
						break;
					default:
					}
				}
			}

			final EnumMap<CompassDirection, Set<Point2D>> walls = new EnumMap<>(CompassDirection.class);
			walls.put(CompassDirection.NORTH, north_walls);
			walls.put(CompassDirection.WEST, rotateRight(walls.get(CompassDirection.NORTH), size));
			walls.put(CompassDirection.SOUTH, rotateRight(walls.get(CompassDirection.WEST), size));
			walls.put(CompassDirection.EAST, rotateRight(walls.get(CompassDirection.SOUTH), size));

			return new ReflectorDish(size, walls, rocks, new AtomicInteger(0));
		}

		public static int calculateLoad(Set<Point2D> rocks, int size) {
			return rocks.stream().mapToInt(p -> size - p.y()).sum();
		}

		public static Set<Point2D> rotateRight(final Set<Point2D> points, final int size) {
			return points.stream().map(p -> new Point2D(size - p.y() - 1, p.x())).collect(Collectors.toSet());
		}

		public ReflectorDish tilt() {
			final Set<Point2D> dir_walls = walls.get(NWSE[directionIndex.get()]);

			for (int x = 0; x < size; x++) {
				// Move every circular rock as far north as possible
				for (int y = 1; y < size; y++) {
					final Point2D p = new Point2D(x, y);
					if (rocks.contains(p)) {
						Point2D last_p = p;
						for (int dy = y - 1; dy >= 0; dy--) {
							final Point2D dp = new Point2D(x, dy);
							if (dir_walls.contains(dp)) {
								break;
							}
							if (!rocks.contains(dp)) {
								rocks.add(dp);
								rocks.remove(last_p);
								last_p = dp;
							}
						}
					}
				}
			}

			return this;
		}

		public void rotate() {
			final Set<Point2D> new_rocks = rotateRight(rocks, size);
			rocks.clear();
			rocks.addAll(new_rocks);
			directionIndex.set(directionIndex.incrementAndGet() % 4);
		}

		public int calculateLoad() {
			return calculateLoad(rocks, size);
		}
	}
}
