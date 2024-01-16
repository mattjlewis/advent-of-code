package com.diozero.aoc.util;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Rectangle;

public class MatrixUtil {
	private MatrixUtil() {
	}

	public static int mask(int x, int y, int width) {
		return 1 << (y * width + x);
	}

	public static boolean isSet(int value, int x, int y, int width) {
		return (value & mask(x, y, width)) != 0;
	}

	public static int convertToBitMask(boolean[][] matrix) {
		final int width = matrix[0].length;

		int value = 0;
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix[y][x]) {
					value |= mask(x, y, width);
				}
			}
		}

		return value;
	}

	public static boolean[][] convertToMatrix(int mask, int width, int height) {
		final boolean[][] matrix = new boolean[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				matrix[y][x] = isSet(mask, x, y, width);
			}
		}

		return matrix;
	}

	public static char[][] initialiseMatrix(int width, int height, char ch) {
		char[][] matrix = new char[height][width];
		final char[] ground_row = Character.toString(ch).repeat(width).toCharArray();
		for (int y = 0; y < height; y++) {
			System.arraycopy(ground_row, 0, matrix[y], 0, width);
		}

		return matrix;
	}

	public static boolean[][] toMatrix(boolean[] data, int width) {
		if (data.length % width != 0) {
			throw new IllegalArgumentException();
		}
		boolean[][] matrix = new boolean[data.length / width][width];
		for (int y = 0; y < data.length / width; y++) {
			System.arraycopy(data, y * width, matrix[y], 0, width);
		}
		return matrix;
	}

	public static boolean compare(boolean[][] array1, boolean[][] array2) {
		if (array1.length != array2.length || array1[0].length != array2[0].length) {
			return false;
		}

		for (int y = 0; y < array1.length; y++) {
			if (Arrays.compare(array1[y], array2[y]) != 0) {
				return false;
			}
		}

		return true;
	}

	public static boolean[][] clone(final boolean[][] matrix) {
		final boolean[][] new_array = new boolean[matrix.length][matrix[0].length];

		for (int y = 0; y < matrix.length; y++) {
			System.arraycopy(matrix[y], 0, new_array[y], 0, new_array[y].length);
		}

		return new_array;
	}

	public static char[][] clone(char[][] matrix) {
		final char[][] new_array = new char[matrix.length][matrix[0].length];

		for (int y = 0; y < matrix.length; y++) {
			System.arraycopy(matrix[y], 0, new_array[y], 0, new_array[y].length);
		}

		return new_array;
	}

	public static Set<Point2D> toPoints(boolean[][] matrix) {
		final Set<Point2D> points = new HashSet<>();

		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				if (matrix[y][x]) {
					points.add(new Point2D(x, y));
				}
			}
		}

		return points;
	}

	public static Set<MutablePoint2D> toMutablePoints(boolean[][] matrix) {
		final Set<MutablePoint2D> points = new HashSet<>();

		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				if (matrix[y][x]) {
					points.add(new MutablePoint2D(x, y));
				}
			}
		}

		return points;
	}

	public static int count(boolean[][] matrix) {
		int count = 0;
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				if (matrix[y][x]) {
					count++;
				}
			}
		}
		return count;
	}

	public static int max(final int[][] matrix) {
		int max = Integer.MIN_VALUE;
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				max = Math.max(max, matrix[y][x]);
			}
		}

		return max;
	}

	public static boolean[][] toMatrix(Set<Point2D> points) {
		final Rectangle bounds = Point2D.getBounds(points);
		final boolean[][] matrix = new boolean[bounds.height()][bounds.width()];
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				matrix[y][x] = points.contains(new Point2D(bounds.topLeft().x() + x, bounds.topLeft().y() + y));
			}
		}

		return matrix;
	}

	public static char[][] toCharMatrix(Set<Point2D> points) {
		return toCharMatrix(points, TextParser.SET_CHAR, TextParser.UNSET_CHAR);
	}

	public static char[][] toCharMatrix(Set<Point2D> points, char setChar, char unsetChar) {
		final Rectangle bounds = Point2D.getBounds(points);
		final char[][] matrix = new char[bounds.height()][bounds.width()];
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				matrix[y][x] = points.contains(new Point2D(bounds.topLeft().x() + x, bounds.topLeft().y() + y))
						? setChar
						: unsetChar;
			}
		}

		return matrix;
	}

	public static Optional<Point2D> find(char[][] grid, char ch) {
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[0].length; x++) {
				if (grid[y][x] == ch) {
					return Optional.of(new Point2D(x, y));
				}
			}
		}
		return Optional.empty();
	}

	// Get the previous value at (x, y) and call the recursive floodFill method
	public static void floodFill(char grid[][], int x, int y, char newValue) {
		final char previous_value = grid[y][x];
		if (previous_value == newValue) {
			return;
		}
		floodFill(grid, x, y, previous_value, newValue);
	}

	private static void floodFill(char grid[][], int x, int y, char previousValue, char newValue) {
		final Deque<Point2D> queue = new ArrayDeque<>();
		final Set<Point2D> visited = new HashSet<>();

		Point2D curr = new Point2D(x, y);
		queue.add(curr);
		visited.add(curr);

		while (!queue.isEmpty()) {
			curr = queue.removeFirst();

			grid[curr.y()][curr.x()] = newValue;

			for (CompassDirection dir : CompassDirection.NESW) {
				final Point2D next = curr.move(dir);
				if (!visited.contains(next) && isValid(grid, next.x(), next.y(), previousValue)) {
					visited.add(next);
					queue.add(next);
				}
			}
		}
	}

	private static boolean isValid(char[][] grid, int x, int y, char previousValue) {
		if (x < 0 || x >= grid[0].length || y < 0 || y >= grid.length) {
			return false;
		}
		if (grid[y][x] != previousValue) {
			return false;
		}

		return true;
	}
}
