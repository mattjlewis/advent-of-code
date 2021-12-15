package com.diozero.aoc2021;

import java.io.IOException;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc2021.util.AocBase;
import com.diozero.aoc2021.util.Dijkstra;
import com.diozero.aoc2021.util.Node;

public class Day15 extends AocBase {
	public static void main(String[] args) {
		new Day15().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		int[][] matrix = AocBase.loadIntegerMatrix(input);

		// Convert the integer matrix into a graph
		Node[][] node_matrix = new Node[matrix.length][matrix[0].length];
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				node_matrix[y][x] = new Node(String.format("%dx%d", Integer.valueOf(x), Integer.valueOf(y)));
			}
		}

		// Calculate the distances between nodes
		calculateDistanceBetweenNodes(node_matrix, matrix);

		Node start_node = node_matrix[0][0];
		Node end_node = node_matrix[node_matrix.length - 1][node_matrix[0].length - 1];

		Dijkstra.calculateShortestPathFromSource(start_node);

		if (Logger.isDebugEnabled()) {
			end_node.getShortestPath()
					.forEach(node -> System.out.format("%s (%d) -> ", node.getName(), node.getDistance()));
			System.out.format("%s (%d)%n", end_node.getName(), end_node.getDistance());
		}

		return end_node.getDistance();
	}

	@Override
	public long part2(Path input) throws IOException {
		int[][] matrix = AocBase.loadIntegerMatrix(input);
		AocBase.printGrid(matrix);

		// Expand the matrix - this could be optimised...
		int expansion = 5;
		int orig_height = matrix.length;
		int orig_width = matrix[0].length;
		int[][] new_matrix = new int[orig_height * expansion][orig_width * expansion];
		for (int grid_y = 0; grid_y < expansion; grid_y++) {
			for (int grid_x = 0; grid_x < expansion; grid_x++) {
				if (grid_y == 0 && grid_x == 0) {
					for (int y = 0; y < orig_height; y++) {
						for (int x = 0; x < orig_width; x++) {
							new_matrix[y][x] = matrix[y][x];
						}
					}
				} else if (grid_x == 0) {
					// Get from the cell above
					for (int y = 0; y < orig_height; y++) {
						for (int x = 0; x < orig_width; x++) {
							int xx = grid_x * orig_height + x;
							int new_val = new_matrix[(grid_y - 1) * orig_height + y][xx] + 1;
							new_matrix[grid_y * orig_height + y][xx] = new_val > 9 ? 1 : new_val;
						}
					}
				} else {
					// Get from the cell to the left
					for (int y = 0; y < orig_height; y++) {
						for (int x = 0; x < orig_width; x++) {
							int yy = grid_y * orig_height + y;
							int new_val = new_matrix[yy][(grid_x - 1) * orig_width + x] + 1;
							new_matrix[yy][grid_x * orig_width + x] = new_val > 9 ? 1 : new_val;
						}
					}
				}
			}
		}
		matrix = new_matrix;
		AocBase.printGrid(matrix);

		// Convert the integer matrix into a graph
		Node[][] node_matrix = new Node[matrix.length][matrix.length];
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				node_matrix[y][x] = new Node(String.format("%dx%d", Integer.valueOf(x), Integer.valueOf(y)));
			}
		}

		// Calculate the distances between nodes
		calculateDistanceBetweenNodes(node_matrix, matrix);

		Node start_node = node_matrix[0][0];
		Node end_node = node_matrix[node_matrix.length - 1][node_matrix[0].length - 1];

		Dijkstra.calculateShortestPathFromSource(start_node);

		if (Logger.isDebugEnabled()) {
			end_node.getShortestPath()
					.forEach(node -> System.out.format("%s (%d) -> ", node.getName(), node.getDistance()));
			System.out.format("%s (%d)%n", end_node.getName(), end_node.getDistance());
		}

		return end_node.getDistance();
	}

	private static void calculateDistanceBetweenNodes(Node[][] nodeMatrix, int[][] matrix) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				if (y == matrix.length - 1 && x == matrix[y].length - 1) {
					continue;
				}

				Node this_node = nodeMatrix[y][x];

				for (int dy = Math.max(0, y - 1); dy <= Math.min(matrix.length - 1, y + 1); dy++) {
					for (int dx = Math.max(0, x - 1); dx <= Math.min(matrix[dy].length - 1, x + 1); dx++) {
						// No diagonals
						if ((x != dx || y != dy) && (x == dx || y == dy)) {
							this_node.addDestination(nodeMatrix[dy][dx], matrix[dy][dx]);
						}
					}
				}
			}
		}
	}
}