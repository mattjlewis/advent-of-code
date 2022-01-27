package com.diozero.aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

public abstract class Day implements Runnable {
	private int year;
	private int day;

	public Day() {
		year = Integer.parseInt(getClass().getPackageName().split("\\.(?=[^.]*$)")[1].substring(1));
		day = Integer.parseInt(getClass().getSimpleName().split("Day(?=\\d+$)")[1]);
	}

	public abstract String name();

	public abstract String part1(final Path input) throws IOException;

	public abstract String part2(final Path input) throws IOException;

	@Override
	public final void run() {
		String s = System.getProperty("perf");
		boolean perf = false;
		int iterations = 10;
		if (s != null) {
			perf = true;

			if (!s.isBlank()) {
				iterations = Integer.parseInt(s);
			}
		}

		String sample_prop = System.getProperty("s");
		if (sample_prop == null) {
			sample_prop = System.getProperty("sample");
		}
		String input = "day" + day;
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
			System.out.format("%d Day %d - Working from sample data set '%s'%n", year, day, input);
		}

		try {
			String[] answers = null;
			if (answers_path.toFile().canRead()) {
				answers = Files.lines(answers_path).filter(str -> !str.isBlank()).toArray(String[]::new);
			}

			if (perf) {
				for (int i = 0; i < iterations; i++) {
					part1(input_path);
				}
			}
			long start = System.currentTimeMillis();
			try {
				String answer = part1(input_path);
				long duration = System.currentTimeMillis() - start;
				checkResult(1, answers, answer, duration);
			} catch (Exception e) {
				Logger.error(e, "Error: {}", e);
			}

			if (perf) {
				for (int i = 0; i < iterations; i++) {
					part2(input_path);
				}
			}
			start = System.currentTimeMillis();
			String answer = part2(input_path);
			long duration = System.currentTimeMillis() - start;
			checkResult(2, answers, answer, duration);
		} catch (IOException e) {
			System.out.println("Error unable to read input '" + input_path + "'");
		}
	}

	private void checkResult(int part, String[] answers, String result, long duration) {
		if (answers == null || answers.length < part) {
			System.out.format("%d Day %d '%s' part %d: %s. Duration: %dms%n", year, day, name(), part, result,
					duration);
		} else {
			if (result != null && result.equals(answers[part - 1])) {
				System.out.format("%d Day %d '%s' part %d - Correct answer: %s. Duration: %,dms%n", year, day, name(),
						part, result, duration);
			} else {
				System.out.format("WRONG: %d Day %d '%s' part %d - Wrong answer (%s), expected: %s. Duration: %,dms%n",
						year, day, name(), part, result, answers[part - 1], duration);
			}
		}
	}
}
