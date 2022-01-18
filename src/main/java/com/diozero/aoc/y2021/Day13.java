package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.geometry.Point2D;
import com.diozero.aoc.util.ArrayUtil;

public class Day13 extends Day {
	public static void main(String[] args) {
		new Day13().run();
	}

	@Override
	public String name() {
		return "Transparent Origami";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Puzzle puzzle = loadData(input);

		// Part 1 calculates the number of dots after one fold
		return Integer.toString(fold(puzzle.matrix(), Arrays.asList(puzzle.folds().get(0))));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Puzzle puzzle = loadData(input);

		// return "AHGCPGAU";
		return Integer.toString(fold(puzzle.matrix(), puzzle.folds()));
	}

	private static Puzzle loadData(final Path input) throws IOException {
		final Iterator<String> it = Files.lines(input).iterator();
		boolean process_points = true;
		final List<Point2D> dots = new ArrayList<>();
		final List<Fold> folds = new ArrayList<>();
		int max_x = Integer.MIN_VALUE;
		int max_y = Integer.MIN_VALUE;
		while (it.hasNext()) {
			String line = it.next();
			if (line.trim().isEmpty()) {
				process_points = false;
				continue;
			}
			if (process_points) {
				String[] x_y_parts = line.split(",");
				Point2D point = new Point2D(Integer.parseInt(x_y_parts[0]), Integer.parseInt(x_y_parts[1]));
				dots.add(point);
				max_x = Math.max(point.x(), max_x);
				max_y = Math.max(point.y(), max_y);
			} else {
				String[] parts = line.split("=");
				folds.add(new Fold(Axis.valueOf(parts[0].split(" ")[2].toUpperCase()), Integer.parseInt(parts[1])));
			}
		}
		Logger.debug("dimensions: {}x{}", max_x + 1, max_y + 1);
		Logger.debug("dots: " + dots);

		// FIXME Optimise by working from the collection of points
		final boolean[][] matrix = new boolean[max_y + 1][max_x + 1];
		dots.forEach(dot -> matrix[dot.y()][dot.x()] = true);

		return new Puzzle(matrix, folds);
	}

	private static int fold(final boolean[][] startMatrix, final List<Fold> folds) {
		boolean[][] matrix = startMatrix.clone();

		for (Fold fold : folds) {
			final boolean[][] new_matrix;

			switch (fold.axis()) {
			case Y:
				// Horizontal fold
				final int new_height = matrix.length - fold.point() - 1;
				Logger.debug("new_height: {} after horizontal fold at point {}", new_height, fold.point());
				if ((matrix.length - 1) / 2 != new_height) {
					// It would appear that all folds are in the middle
					Logger.warn("Non-middle fold! " + fold);
				}
				// FIXME Optimise by working from a collection of points
				new_matrix = new boolean[new_height][matrix[0].length];
				for (int y = 0; y < new_height; y++) {
					for (int x = 0; x < matrix[y].length; x++) {
						new_matrix[y][x] = matrix[y][x] | matrix[matrix.length - 1 - y][x];
					}
				}
				break;
			case X:
			default:
				// Vertical fold
				final int new_width = matrix[0].length - fold.point() - 1;
				Logger.debug("new_width: {} after vertical fold at point {}", new_width, fold.point());
				if ((matrix[0].length - 1) / 2 != new_width) {
					// It would appear that all folds are in the middle
					Logger.warn("Non-middle fold! " + fold);
				}
				// FIXME Optimise by working from a collection of points
				new_matrix = new boolean[matrix.length][new_width];
				for (int y = 0; y < matrix.length; y++) {
					for (int x = 0; x < new_width; x++) {
						new_matrix[y][x] = matrix[y][x] | matrix[y][matrix[y].length - 1 - x];
					}
				}
			}

			matrix = new_matrix;

			if (Logger.isDebugEnabled() && matrix.length < 20) {
				System.out.println();
				ArrayUtil.print(matrix, '█', ' ');
			}
		}

		if (matrix.length < 50 && matrix[0].length < 50) {
			// Part 2 should print "AHGCPGAU" (40x6 matrix)
			System.out.println();
			ArrayUtil.print(matrix, '█', ' ');
			System.out.println();
		}

		int count = 0;
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				if (matrix[y][x]) {
					count++;
				}
			}
		}

		return count;
	}

	private static record Puzzle(boolean[][] matrix, List<Fold> folds) {
		//
	}

	private enum Axis {
		X, Y;
	}

	private static record Fold(Axis axis, int point) {
		//
	}
}
