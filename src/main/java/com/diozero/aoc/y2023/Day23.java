package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Graph;
import com.diozero.aoc.algorithm.GraphNode;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.PrintUtil;
import com.diozero.aoc.util.TextParser;

public class Day23 extends Day {
	private static final char WALL = TextParser.SET_CHAR;
	private static final char GROUND = TextParser.UNSET_CHAR;
	private static final char SLOPE_RIGHT = '>';
	private static final char SLOPE_LEFT = '<';
	private static final char SLOPE_UP = '^';
	private static final char SLOPE_DOWN = 'v';

	public static void main(String[] args) {
		new Day23().run();
	}

	@Override
	public String name() {
		return "A Long Walk";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final char[][] maze = TextParser.loadCharMatrix(input);
		final int width = maze[0].length;
		final int height = maze.length;

		GraphNode<Integer, Point2D> start = null;
		GraphNode<Integer, Point2D> end = null;
		final Graph<Integer, Point2D> graph = new Graph<>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (maze[y][x] != WALL) {
					final GraphNode<Integer, Point2D> node = graph.getOrPut(new Point2D(x, y), p -> p.identity(width));
					if (y == 0) {
						start = node;
					} else if (y == maze.length - 1) {
						end = node;
					}

					// Find the neighbours
					for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
							// Cannot move diagonally
							if (dx == x && dy == y || dx != x && dy != y || maze[dy][dx] == WALL) {
								continue;
							}

							// If currently on GROUND, can move any non-wall direction
							if (maze[y][x] == GROUND // Can move any direction when on ground
									|| maze[y][x] == SLOPE_RIGHT && dx > x // Must go right when on >
									|| maze[y][x] == SLOPE_LEFT && dx < x // Must go left when on <
									|| maze[y][x] == SLOPE_UP && dy < y // Must go up when on ^
									|| maze[y][x] == SLOPE_DOWN && dy > y // Must go down when on v
							) {
								// Cannot move right to '<' slope
								// Cannot move 'up' to 'v' slope
								// Cannot move 'down' to '^' slope
								if (maze[dy][dx] == GROUND //
										|| maze[dy][dx] == SLOPE_RIGHT && dx > x // Can only move right to '>' slope
										|| maze[dy][dx] == SLOPE_LEFT && dx < x // Can only move left to '<' slope
										|| maze[dy][dx] == SLOPE_UP && dy < y // Can only move up to '^' slope
										|| maze[dy][dx] == SLOPE_DOWN && dy > y // Can only move up to '^' slope
								) {
									node.addNeighbour(graph.getOrPut(new Point2D(dx, dy), p -> p.identity(width)), 1);
								}
							}
						}
					}
				}
			}
		}

		if (start == null || end == null) {
			throw new IllegalStateException("Unable to find start or end");
		}

		PrintUtil.print(graph.nodes().stream().map(GraphNode::value).toList());
		graph.reduce(Set.of(start.value(), end.value()), false);
		PrintUtil.print(graph.nodes().stream().map(GraphNode::value).toList());

		boolean result = true;
		/*-
		boolean result = Dijkstra.findPath(start, end);
		
		final Set<Point2D> path = new HashSet<>();
		GraphNode<Integer, Point2D> n = end;
		do {
			path.add(n.value());
			n = n.getParent();
		} while (n != null);
		System.out.println("Shortest path:");
		PrintUtil.print(path);
		 */

		System.out.println(result + ", shortest path: " + end.cost());

		return Integer.toString(end.cost());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(0);
	}
}
