package com.diozero.aoc.algorithm;

import java.io.IOException;
import java.nio.file.Path;

import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.TextParser;

public record Maze(Graph<String, Point2D> graph, GraphNode<String, Point2D> start, GraphNode<String, Point2D> end) {

	private static final char WALL = TextParser.SET_CHAR;

	public static Maze load(Path input) throws IOException {
		final char[][] grid = TextParser.loadCharMatrix(input);
		final int width = grid[0].length;
		final int height = grid.length;

		final Graph<String, Point2D> graph = new Graph<>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (grid[y][x] != WALL) {
					final GraphNode<String, Point2D> node = graph.getOrPut(new Point2D(x, y), Point2D::toString);
					for (int dy = Math.max(0, y - 1); dy <= Math.min(height - 1, y + 1); dy++) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(width - 1, x + 1); dx++) {
							// Ignore x,y, diagonals and walls
							if (dy == y && dx == x || dy != y && dx != x || grid[dy][dx] == WALL) {
								continue;
							}
							node.addNeighbour(graph.getOrPut(new Point2D(dx, dy), Point2D::toString), 1);
						}
					}
				}
			}
		}

		final GraphNode<String, Point2D> start = graph.getOrPut(MatrixUtil.find(grid, 'S').orElseThrow(),
				Point2D::toString);
		final GraphNode<String, Point2D> end = graph.getOrPut(MatrixUtil.find(grid, 'E').orElseThrow(),
				Point2D::toString);

		return new Maze(graph, start, end);
	}
}
