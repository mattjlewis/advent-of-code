package com.diozero.aoc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.diozero.aoc.geometry.Point2D;

public class ArrayUtil {
	private ArrayUtil() {
	}

	public static boolean[][] clone(final boolean[][] matrix) {
		boolean[][] new_array = new boolean[matrix.length][matrix[0].length];

		for (int y = 0; y < matrix.length; y++) {
			System.arraycopy(matrix[y], 0, new_array[y], 0, new_array[y].length);
		}

		return new_array;
	}

	public static char[][] clone(char[][] matrix) {
		char[][] new_array = new char[matrix.length][matrix[0].length];

		for (int y = 0; y < matrix.length; y++) {
			System.arraycopy(matrix[y], 0, new_array[y], 0, new_array[y].length);
		}

		return new_array;
	}

	public static Set<Point2D> toPoints(boolean[][] matrix) {
		Set<Point2D> points = new HashSet<>();
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				if (matrix[y][x]) {
					points.add(new Point2D(x, y));
				}
			}
		}
		return points;
	}

	public static <T> Stream<List<T>> permutations(List<T> values) {
		return permutations(values, values.size());
	}

	private static <T> Stream<List<T>> permutations(List<T> values, int r) {
		if (r <= 0) {
			return Stream.empty();
		}

		if (r == 1) {
			return values.stream().map(Arrays::asList);
		}

		if (r == 2) {
			return values.stream().flatMap(e1 -> values.stream() // e1: refers to an element of c
					.filter(e2 -> !e1.equals(e2)) // e2: refers to an element of c
					.map(e2 -> Arrays.asList(e1, e2)));
		}

		return permutations(values, r - 1).flatMap(l -> values.stream().filter(e -> l.contains(e) == false).map(e -> {
			List<T> out = new ArrayList<>();
			out.addAll(l);
			out.add(e);
			return out;
		}));
	}
}
