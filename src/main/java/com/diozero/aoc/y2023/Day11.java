package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.TextParser;

public class Day11 extends Day {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public String name() {
		return "Cosmic Expansion";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(getShortestPathSum(input, 2));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(getShortestPathSum(input, 1_000_000));
	}

	private static long getShortestPathSum(Path input, int expansionRate) throws IOException {
		final boolean[][] grid = TextParser.loadBooleanArray(input, '#');
		final Set<MutablePoint2D> mutable_galaxies = MatrixUtil.toMutablePoints(grid);

		// Find empty rows
		final List<Integer> empty_rows = new ArrayList<>();
		for (int y = 0; y < grid.length; y++) {
			boolean found = false;
			for (int x = 0; x < grid[0].length && !found; x++) {
				if (grid[y][x]) {
					found = true;
				}
			}
			if (!found) {
				empty_rows.add(Integer.valueOf(y));
			}
		}
		// Find empty columns
		final List<Integer> empty_columns = new ArrayList<>();
		for (int x = 0; x < grid[0].length; x++) {
			boolean found = false;
			for (int y = 0; y < grid.length && !found; y++) {
				if (grid[y][x]) {
					found = true;
				}
			}
			if (!found) {
				empty_columns.add(Integer.valueOf(x));
			}
		}

		// Increase the space between galaxies where there are empty rows
		mutable_galaxies.forEach(galaxy -> galaxy.translate(0,
				(int) empty_rows.stream().filter(y -> y.intValue() < galaxy.y()).count() * (expansionRate - 1)));
		// Increase the space between galaxies where there are empty columns
		mutable_galaxies.forEach(galaxy -> galaxy.translate(
				(int) empty_columns.stream().filter(x -> x.intValue() < galaxy.x()).count() * (expansionRate - 1), 0));

		final Set<Point2D> galaxies = mutable_galaxies.stream().map(MutablePoint2D::immutable)
				.collect(Collectors.toSet());

		// Find every unique pair of galaxies
		final Set<Point2dPair> pairs = new HashSet<>();
		long distance_sum = 0;
		for (Point2D start : galaxies) {
			for (Point2D destination : galaxies) {
				if (destination.equals(start)) {
					continue;
				}
				final Point2dPair pair = new Point2dPair(start, destination);
				if (!pairs.contains(pair)) {
					pairs.add(pair);
					distance_sum += pair.shortestPath();
				}
			}
		}

		return distance_sum;
	}

	private static record Point2dPair(Point2D start, Point2D destination) {
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Point2dPair other) {
				return start.equals(other.start) && destination.equals(other.destination)
						|| start.equals(other.destination) && destination.equals(other.start);
			}

			return false;
		}

		public int shortestPath() {
			return start.manhattanDistance(destination);
		}

		@Override
		public int hashCode() {
			return start.hashCode() + destination.hashCode();
		}
	}
}
