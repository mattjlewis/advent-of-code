package com.diozero.aoc2021.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AocBase {
	public void run() {
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
		Path input_path = Path.of(input + ".txt");
		Path answers_path = Path.of(input + "_answers.txt");

		if (!sample.isEmpty()) {
			System.out.println(day + " - Working from sample data set");
		}

		try {
			long[] answers = null;
			if (answers_path.toFile().canRead()) {
				answers = Files.lines(answers_path).mapToLong(Long::valueOf).toArray();
			}

			checkResult(day, 1, answers, part1(input_path));
			checkResult(day, 2, answers, part2(input_path));
		} catch (IOException e) {
			System.out.println("Error unable to read input '" + input_path + "'");
		}
	}

	private static void checkResult(String day, int part, long[] answers, long result) {
		if (answers == null || answers.length < part) {
			System.out.format("%s part %d: %d%n", day, part, result);
		} else {
			if (result == answers[part - 1]) {
				System.out.format("%s part %d - Correct answer: %d%n", day, part, result);
			} else {
				System.out.format("%s part %d - Wrong answer (%d), expected: %d%n", day, part, result,
						answers[part - 1]);
			}
		}
	}

	protected abstract long part1(Path input) throws IOException;

	protected abstract long part2(Path input) throws IOException;
}
