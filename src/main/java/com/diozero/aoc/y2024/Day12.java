package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day12 extends Day {
	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public String name() {
		return "Garden Groups";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(getRegions(input).values().stream().flatMap(Collection::stream)
				.mapToInt(region -> region.plots.size() * region.perimeter()).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(getRegions(input).values().stream().flatMap(Collection::stream)
				.mapToInt(region -> region.plots.size() * region.corners()).sum());
	}

	private static Map<Character, List<Region>> getRegions(Path input) throws IOException {
		char[][] grid = TextParser.loadCharMatrix(input);
		int gridSize = grid.length;
		Map<Character, List<Region>> regions = new HashMap<>();

		for (int y = 0; y < gridSize; y++) {
			for (int x = 0; x < gridSize; x++) {
				char type = grid[y][x];
				Point2D point = new Point2D(x, y);
				regions.computeIfAbsent(type, k -> new ArrayList<>());
				var regionsToMerge = new ArrayList<Region>();

				for (Region region : regions.get(type)) {
					if (region.isAdjacent(point)) {
						regionsToMerge.add(region);
					}
				}

				if (regionsToMerge.isEmpty()) {
					regions.get(type).add(new Region(new HashSet<>(List.of(point))));
				} else {
					Region mainRegion = regionsToMerge.get(0);
					mainRegion.plots.add(point);
					for (int i = 1; i < regionsToMerge.size(); i++) {
						mainRegion.merge(regionsToMerge.get(i));
						regions.get(type).remove(regionsToMerge.get(i));
					}
				}
			}
		}
		return regions;
	}

	static boolean containsNone(Set<Point2D> set, List<Point2D> points) {
		return points.stream().noneMatch(set::contains);
	}

	record Region(Set<Point2D> plots) {
		boolean isAdjacent(Point2D point) {
			return CompassDirection.NESW.stream().map(direction -> point.move(direction)).anyMatch(plots::contains);
		}

		void merge(Region other) {
			plots.addAll(other.plots);
		}

		int perimeter() {
			int perimeter = 0;
			for (Point2D plot : plots) {
				for (CompassDirection direction : CompassDirection.NESW) {
					Point2D neighbour = plot.move(direction);
					if (!plots.contains(neighbour)) {
						perimeter++;
					}
				}
			}
			return perimeter;
		}

		int corners() {
			int total = 0;

			for (final Point2D point : plots) {
				int corners = 0;
				Point2D north = point.move(CompassDirection.NORTH);
				Point2D south = point.move(CompassDirection.SOUTH);
				Point2D east = point.move(CompassDirection.EAST);
				Point2D west = point.move(CompassDirection.WEST);
				Point2D northEast = north.move(CompassDirection.EAST);
				Point2D northWest = north.move(CompassDirection.WEST);
				Point2D southEast = south.move(CompassDirection.EAST);
				Point2D southWest = south.move(CompassDirection.WEST);

				if (containsNone(plots, List.of(west, north))
						|| !plots.contains(northWest) && plots.containsAll(List.of(west, north))) {
					corners++;
				}
				if (containsNone(plots, List.of(east, north))
						|| !plots.contains(northEast) && plots.containsAll(List.of(east, north))) {
					corners++;
				}
				if (containsNone(plots, List.of(east, south))
						|| !plots.contains(southEast) && plots.containsAll(List.of(east, south))) {
					corners++;
				}
				if (containsNone(plots, List.of(west, south))
						|| !plots.contains(southWest) && plots.containsAll(List.of(west, south))) {
					corners++;
				}
				total += corners;
			}

			return total;
		}
	}
}
