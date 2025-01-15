package com.diozero.aoc.util;

import java.util.Collection;
import java.util.Map;

import com.diozero.aoc.function.ToCharFunction;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.geometry.Rectangle;

public class PrintUtil {
	private PrintUtil() {
	}

	public static final char FILLED_PIXEL = '█';
	public static final char BLANK_PIXEL = ' ';

	public static void print(final int[][] matrix) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				System.out.print(matrix[y][x]);
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(final int[][] matrix, String intFormat) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				System.out.format(intFormat, Integer.valueOf(matrix[y][x]));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(final boolean[][] matrix) {
		print(matrix, FILLED_PIXEL, BLANK_PIXEL);
	}

	public static void print(final boolean[][] matrix, char trueChar, char falseChar) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				System.out.print(matrix[y][x] ? trueChar : falseChar);
			}
			System.out.println();
		}
	}

	public static void print(final char[][] matrix) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				System.out.print(matrix[y][x]);
			}
			System.out.println();
		}
	}

	public static void print(final Collection<Point2D> points) {
		print(points, FILLED_PIXEL, BLANK_PIXEL);
	}

	public static void print(Collection<Point2D> points, char filledPixel, char blankPixel) {
		if (points.isEmpty()) {
			return;
		}

		final Rectangle bounds = Point2D.getBounds(points);

		for (int y = bounds.topLeft().y(); y <= bounds.bottomRight().y(); y++) {
			for (int x = bounds.topLeft().x(); x <= bounds.bottomRight().x(); x++) {
				if (points.contains(new Point2D(x, y))) {
					System.out.print(filledPixel);
				} else {
					System.out.print(blankPixel);
				}
			}
			System.out.println();
		}
	}

	public static <T> void print(final Map<Point2D, T> grid, final char defaultValue,
			final ToCharFunction<T> cellFunction) {
		final Rectangle bounds = Point2D.getBounds(grid.keySet());

		for (int y = bounds.topLeft().y(); y <= bounds.bottomRight().y(); y++) {
			for (int x = bounds.topLeft().x(); x <= bounds.bottomRight().x(); x++) {
				T val = grid.get(new Point2D(x, y));
				if (val == null) {
					System.out.print(defaultValue);
				} else {
					System.out.print(cellFunction.applyAsChar(val));
				}
			}
			System.out.println();
		}
	}
}
