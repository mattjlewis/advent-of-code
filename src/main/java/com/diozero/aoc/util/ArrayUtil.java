package com.diozero.aoc.util;

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

	public static void print(char[][] matrix) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				System.out.print(matrix[y][x]);
			}
			System.out.println();
		}
	}

	public static void print(int[][] matrix) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				System.out.print(matrix[y][x]);
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void print(final boolean[][] matrix, char trueChar, char falseChar) {
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				System.out.print(matrix[y][x] ? trueChar : falseChar);
			}
			System.out.println();
		}
	}
}
