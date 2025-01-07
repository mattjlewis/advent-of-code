package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Deque;

import com.diozero.aoc.Day;
import com.diozero.aoc.algorithm.Maze;
import com.diozero.aoc.algorithm.dijkstra.Dijkstra;
import com.diozero.aoc.geometry.Point2D;

public class Day20 extends Day {
	public static void main(String[] args) {
		new Day20().run();
	}

	@Override
	public String name() {
		return "Race Condition";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(solve(input, isSample() ? 1 : 100, 2));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(solve(input, isSample() ? 50 : 100, 20));
	}

	private static int solve(Path input, int minTimeSaved, int maxDist) throws IOException {
		final Maze maze = Maze.load(input);
		if (!Dijkstra.findPath(maze.start(), maze.end())) {
			throw new IllegalStateException("Unable to find path through maze");
		}

		final Deque<Point2D> path = maze.end().path();
		final Point2D[] path_nodes = path.toArray(new Point2D[path.size()]);

		int cheat_count = 0;
		for (int i = 0; i < path_nodes.length - 1; i++) {
			final Point2D pi = path_nodes[i];
			for (int j = i + 1; j < path_nodes.length; j++) {
				final Point2D pj = path_nodes[j];
				final int dist = pj.manhattanDistance(pi);
				if (dist <= maxDist && (j - i - dist) >= minTimeSaved) {
					cheat_count++;
				}
			}
		}

		return cheat_count;
	}
}
