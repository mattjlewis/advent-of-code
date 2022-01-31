package com.diozero.aoc.util;

import java.util.Iterator;
import java.util.Map;

import com.diozero.aoc.geometry.Point2D;

public class PrintUtil {
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
			for (int x = 0; x < matrix[0].length; x++) {
				System.out.print(matrix[y][x]);
			}
			System.out.println();
		}
	}

	public static <T> void print(Map<Point2D, T> grid, char defaultValue, ToCharFunction<T> cellFunction) {
		int min_x = Integer.MAX_VALUE;
		int max_x = Integer.MIN_VALUE;
		int min_y = Integer.MAX_VALUE;
		int max_y = Integer.MIN_VALUE;
		Iterator<Point2D> it = grid.keySet().iterator();
		while (it.hasNext()) {
			Point2D p = it.next();

			min_x = Math.min(min_x, p.x());
			max_x = Math.max(max_x, p.x());
			min_y = Math.min(min_y, p.y());
			max_y = Math.max(max_y, p.y());
		}

		for (int y = min_y; y <= max_y; y++) {
			for (int x = min_x; x <= max_x; x++) {
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
