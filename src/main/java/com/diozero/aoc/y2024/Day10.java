package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;
import com.diozero.aoc.util.Tuple2;

public class Day10 extends Day {
	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public String name() {
		return "Hoof It";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Tuple2<int[][], List<Point2D>> puzzle = load(input);

		return Integer.toString(
				puzzle.second().stream().mapToInt(p -> exploreTrail(p, puzzle.first(), new HashSet<>())).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Tuple2<int[][], List<Point2D>> puzzle = load(input);

		return Integer.toString(
				puzzle.second().stream().mapToInt(p -> exploreTrail(p, puzzle.first(), new ArrayList<>())).sum());
	}

	private static Tuple2<int[][], List<Point2D>> load(Path input) throws IOException {
		final int[][] topo_map = TextParser.loadIntMatrix(input);

		final List<Point2D> starting_points = new ArrayList<>();
		for (int y = 0; y < topo_map.length; y++) {
			for (int x = 0; x < topo_map[0].length; x++) {
				if (topo_map[y][x] == 0) {
					starting_points.add(new Point2D(x, y));
				}
			}
		}

		return new Tuple2<>(topo_map, starting_points);
	}

	private static int exploreTrail(Point2D position, int[][] topoMap, Collection<Point2D> trailEnds) {
		if (topoMap[position.y()][position.x()] == 9) {
			trailEnds.add(position);
		} else {
			CompassDirection.NESW.stream().map(dir -> position.move(dir))
					.filter(next -> next.inBounds(topoMap[0].length, topoMap.length)
							&& topoMap[next.y()][next.x()] - topoMap[position.y()][position.x()] == 1)
					.forEach(next -> exploreTrail(next, topoMap, trailEnds));
		}

		return trailEnds.size();
	}
}
