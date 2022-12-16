package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Path;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.TextParser;

public class Day12 extends Day {
	private static final char START = 'S';
	private static final char END = 'E';
	private static final char LOWEST_ELEVATION = 'a';
	private static final char HIGHEST_ELEVATION = 'z';

	public static void main(String[] args) {
		new Day12().run();
	}

	@Override
	public String name() {
		return "Hill Climbing Algorithm";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final ElevationMap map = ElevationMap.load(input, false);

		// Find the shortest path from the END to START node
		if (!Dijkstra.findPath(map.end, map.start)) {
			throw new IllegalStateException("No path found");
		}

		return Integer.toString(map.start.cost());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final ElevationMap map = ElevationMap.load(input, true);

		// Populate all of the paths that are accessible from the END node
		Dijkstra.findRoutes(map.end);

		// Find the 'a' point with the shortest path from the END node
		return Integer.toString(map.graph.nodes().stream().filter(node -> map.getElevation(node.value()) == 0)
				.mapToInt(GraphNode::cost).min().orElseThrow());
	}

	private static record ElevationMap(char[][] elevationMap, Graph<Integer, Point2D> graph,
			GraphNode<Integer, Point2D> start, GraphNode<Integer, Point2D> end) {
		public static ElevationMap load(final Path input, final boolean part2) throws IOException {
			final char[][] elevation_map = TextParser.loadCharMatrix(input);
			final int width = elevation_map[0].length;
			final int height = elevation_map.length;

			final Graph<Integer, Point2D> graph = new Graph<>();

			GraphNode<Integer, Point2D> start = null;
			GraphNode<Integer, Point2D> end = null;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					final GraphNode<Integer, Point2D> node = graph.getOrPut(new Point2D(x, y),
							p -> idFunction(p, width));
					if (elevation_map[y][x] == END) {
						end = node;
						elevation_map[y][x] = HIGHEST_ELEVATION;
					} else if (elevation_map[y][x] == START) {
						start = node;
						elevation_map[y][x] = LOWEST_ELEVATION;
					}

					if (part2) {
						// For part2, the lowest elevation points have no neighbours as we're finding
						// the shortest path to any 'a' point
						if (elevation_map[y][x] == LOWEST_ELEVATION) {
							continue;
						}
					}

					// Find the neighbours
					for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
							if (dx == x && dy == y || dx != x && dy != y) {
								continue;
							}
							// Note part 2 must be done in reverse (end to start)
							// Part 1 can be done either way so do in reverse to be compatible
							if (getElevation(elevation_map[y][x]) - getElevation(elevation_map[dy][dx]) <= 1) {
								node.addNeighbour(graph.getOrPut(new Point2D(dx, dy), p -> idFunction(p, width)), 1);
							}
						}
					}
				}
			}

			return new ElevationMap(elevation_map, graph, start, end);
		}

		public int getElevation(Point2D p) {
			return elevationMap[p.y()][p.x()] - LOWEST_ELEVATION;
		}

		private static int getElevation(char ch) {
			// START has lowest elevation 'a' hence can go to 'a' or 'b'
			// END has highest elevation 'z' hence can be reached from 'y' or 'z'
			// Still have to deal with S/E chars despite replacing them - the dy/dx loop
			return ch == START ? 0 : ch == END ? HIGHEST_ELEVATION - LOWEST_ELEVATION : ch - LOWEST_ELEVATION;
		}

		private static Integer idFunction(Point2D p, int width) {
			return Integer.valueOf(p.x() + p.y() * width);
		}
	}
}
