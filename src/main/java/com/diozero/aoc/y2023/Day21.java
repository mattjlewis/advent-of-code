package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day21 extends Day {
	private static final char ROCK = TextParser.SET_CHAR;
	private static final char START = 'S';
	private static final int PART2_TARGET_STEPS = 26_501_365;

	public static void main(String[] args) {
		new Day21().run();
	}

	@Override
	public String name() {
		return "Step Counter";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Garden garden = Garden.load(input);

		Set<Point2D> steps = new HashSet<>();
		steps.add(garden.start);
		for (int i = 0; i < (isSample() ? 6 : 64); i++) {
			final Set<Point2D> new_steps = steps.stream()
					.flatMap(p -> CompassDirection.NESW.stream().map(dir -> p.move(dir)))
					.filter(p -> !garden.rocks.contains(p)).collect(Collectors.toSet());
			steps = new_steps;
		}

		return Integer.toString(steps.size());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Garden garden = Garden.load(input);

		final Map<Point2D, Integer> visited = new HashMap<>();
		visited.put(garden.start, Integer.valueOf(0));

		Set<Point2D> steps = new HashSet<>();
		steps.add(garden.start);
		for (int i = 1; i <= garden.size; i++) {
			final Set<Point2D> new_steps = steps.stream()
					.flatMap(p -> CompassDirection.NESW.stream().map(dir -> p.move(dir)))
					.filter(p -> p.inBounds(0, 0, garden.size, garden.size)).filter(p -> !garden.rocks.contains(p))
					.filter(p -> !visited.containsKey(p)).collect(Collectors.toSet());
			final Integer int_i = Integer.valueOf(i);
			new_steps.forEach(p -> visited.put(p, int_i));

			steps = new_steps;
		}

		final int[] values = visited.values().stream().mapToInt(Integer::intValue).toArray();
		final long odd = Arrays.stream(values).filter(i -> i % 2 == 1).count();
		final long even = Arrays.stream(values).filter(i -> i % 2 == 0).count();
		final long odd_corner = Arrays.stream(values).filter(i -> i % 2 == 1 && i > garden.size / 2).count();
		final long even_corner = Arrays.stream(values).filter(i -> i % 2 == 0 && i > garden.size / 2).count();
		// XXX For some reason this doesn't give the right answer for the sample dataset
		final long n = (isSample() ? 5000 : PART2_TARGET_STEPS) / garden.size;

		return Long.toString(((n + 1) * (n + 1) * odd) + (n * n * even) - ((n + 1) * odd_corner) + (n * even_corner));
	}

	private static record Garden(int size, Set<Point2D> rocks, Point2D start) {
		public static Garden load(Path input) throws IOException {
			Point2D start = null;
			final Set<Point2D> rocks = new HashSet<>();
			final char[][] grid = TextParser.loadCharMatrix(input);
			for (int y = 0; y < grid.length; y++) {
				for (int x = 0; x < grid[0].length; x++) {
					switch (grid[y][x]) {
					case ROCK -> rocks.add(new Point2D(x, y));
					case START -> start = new Point2D(x, y);
					}
				}
			}

			return new Garden(grid.length, rocks, start);
		}
	}
}
