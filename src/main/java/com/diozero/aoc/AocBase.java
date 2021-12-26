package com.diozero.aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.tinylog.Logger;

public abstract class AocBase {
	private static int year;

	public AocBase() {
		year = Integer.parseInt(getClass().getPackageName().split("\\.(?=[^.]*$)")[1].substring(1));
	}

	public final void run() {
		if (System.getProperty("selftest") != null) {
			selfTest();
		}

		String s = System.getProperty("perf");
		boolean perf = false;
		int iterations = 10;
		if (s != null) {
			perf = true;

			if (!s.isBlank()) {
				iterations = Integer.parseInt(s);
			}
		}

		String day = getClass().getSimpleName();
		String sample_prop = System.getProperty("sample");
		String input = day.toLowerCase();
		if (sample_prop != null) {
			if (!sample_prop.isEmpty()) {
				input = input + "_" + sample_prop;
			}
		}
		Path input_folder = Path.of(String.format("src/main/resources/input/%d%s", Integer.valueOf(year),
				sample_prop == null ? "" : "_samples"));
		Path input_path = input_folder.resolve(input + ".txt");
		Path answers_path = input_folder.resolve(input + "_answers.txt");

		if (sample_prop != null) {
			System.out.println(year + " " + day + " - Working from sample data set '" + input + "'");
		}

		try {
			long[] answers = null;
			if (answers_path.toFile().canRead()) {
				answers = Files.lines(answers_path).mapToLong(Long::valueOf).toArray();
			}

			if (perf) {
				for (int i = 0; i < iterations; i++) {
					part1(input_path);
				}
			}
			long start = System.currentTimeMillis();
			long answer = part1(input_path);
			long duration = System.currentTimeMillis() - start;
			checkResult(day, 1, answers, answer, duration);

			if (perf) {
				for (int i = 0; i < iterations; i++) {
					part2(input_path);
				}
			}
			start = System.currentTimeMillis();
			answer = part2(input_path);
			duration = System.currentTimeMillis() - start;
			checkResult(day, 2, answers, answer, duration);
		} catch (IOException e) {
			System.out.println("Error unable to read input '" + input_path + "'");
		}
	}

	private static void checkResult(String day, int part, long[] answers, long result, long duration) {
		if (answers == null || answers.length < part) {
			System.out.format("%d %s part %d: %d. Duration: %dms%n", year, day, part, result, duration);
		} else {
			if (result == answers[part - 1]) {
				System.out.format("%d %s part %d - Correct answer: %d. Duration: %dms%n", year, day, part, result,
						duration);
			} else {
				System.out.format("%d %s part %d - Wrong answer (%d), expected: %d. Duration: %dms%n", year, day, part,
						result, answers[part - 1], duration);
			}
		}
	}

	public void selfTest() {
		//
	}

	public abstract long part1(Path input) throws IOException;

	public abstract long part2(Path input) throws IOException;

	public static int[][] loadIntegerMatrix(Path input) throws IOException {
		// Note the lazy conversion from ASCII character code to integer
		final int[][] matrix = Files.lines(input).map(line -> line.chars().map(c -> c - 48).toArray())
				.toArray(int[][]::new);

		if (Logger.isDebugEnabled()) {
			// Print the matrix if not too big
			if (matrix.length < 20) {
				for (int[] row : matrix) {
					Logger.debug("matrix: {}", Arrays.toString(row));
				}
			}
		}

		return matrix;
	}

	public static void printGrid(int[][] matrix) {
		if (!Logger.isDebugEnabled()) {
			return;
		}
		for (int y = 0; y < matrix.length; y++) {
			for (int x = 0; x < matrix[y].length; x++) {
				System.out.format("%3d", Integer.valueOf(matrix[y][x]));
			}
			System.out.println();
		}
		System.out.println();
	}
}
