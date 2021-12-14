package com.diozero.aoc2021.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AocBase {
	public void run() {
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
		String sample = System.getProperty("sample");
		if (sample == null) {
			sample = "";
		} else if (sample.isEmpty()) {
			sample = "_sample";
		} else {
			sample = "_" + sample;
		}
		String input = day.toLowerCase() + sample;
		Path input_folder = Path.of("src/main/resources/input");
		Path input_path = input_folder.resolve(input + ".txt");
		Path answers_path = input_folder.resolve(input + "_answers.txt");

		if (!sample.isEmpty()) {
			System.out.println(day + " - Working from sample data set");
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
			System.out.format("%s part %d: %d. Duration: %dms%n", day, part, result, duration);
		} else {
			if (result == answers[part - 1]) {
				System.out.format("%s part %d - Correct answer: %d. Duration: %dms%n", day, part, result, duration);
			} else {
				System.out.format("%s part %d - Wrong answer (%d), expected: %d. Duration: %dms%n", day, part, result,
						answers[part - 1], duration);
			}
		}
	}

	public abstract long part1(Path input) throws IOException;

	public abstract long part2(Path input) throws IOException;
}
