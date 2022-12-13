package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Path;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.CompassDirection;
import com.diozero.aoc.geometry.MutablePoint2D;
import com.diozero.aoc.util.MatrixUtil;
import com.diozero.aoc.util.TextParser;

public class Day8 extends Day {
	public static void main(String[] args) {
		new Day8().run();
	}

	@Override
	public String name() {
		return "Treetop Tree House";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int[][] matrix = TextParser.loadIntMatrix(input);

		final boolean[][] visible = new boolean[matrix.length][matrix[0].length];
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[0].length; x++) {
				visible[y][x] = (x == 0 || x == matrix[0].length - 1 || y == 0 || y == matrix.length - 1)
						|| isVisible(matrix, x, y);
			}
		}

		return Integer.toString(MatrixUtil.count(visible));
	}

	private static boolean isVisible(int[][] matrix, int x, int y) {
		boolean visible = true;

		for (CompassDirection dir : CompassDirection.NESW) {
			visible = true;
			final MutablePoint2D p = new MutablePoint2D(x, y);
			do {
				p.translate(dir);
				visible = matrix[y][x] > matrix[p.y()][p.x()];
			} while (visible && p.x() > 0 & p.x() < matrix[0].length - 1 && p.y() > 0 & p.y() < matrix.length - 1);

			if (visible) {
				break;
			}
		}

		return visible;
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int[][] matrix = TextParser.loadIntMatrix(input);

		final int[][] scenic_scores = new int[matrix.length][matrix[0].length];
		// Assume that the edges don't have the highest scenic score
		for (int y = 1; y < matrix.length - 1; y++) {
			for (int x = 1; x < matrix[0].length - 1; x++) {
				scenic_scores[y][x] = calcScenicScore(matrix, x, y);
			}
		}

		return Integer.toString(MatrixUtil.max(scenic_scores));
	}

	private static int calcScenicScore(int[][] matrix, int x, int y) {
		int scenic_score = 1;
		for (CompassDirection dir : CompassDirection.NESW) {
			final MutablePoint2D p = new MutablePoint2D(x, y);
			int s = 0;
			do {
				s++;
				p.translate(dir);
			} while (p.x() > 0 && p.x() < matrix[0].length - 1 && p.y() > 0 && p.y() < matrix.length - 1
					&& matrix[y][x] > matrix[p.y()][p.x()]);
			scenic_score *= s;
		}

		return scenic_score;
	}
}
