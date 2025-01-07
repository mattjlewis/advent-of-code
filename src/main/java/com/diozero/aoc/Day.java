package com.diozero.aoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

public abstract class Day {
	public static final String NOT_APPLICABLE = "N/A";

	private int year;
	private int day;
	private boolean sample;

	public Day() {
		year = Integer.parseInt(getClass().getPackageName().split("\\.(?=[^.]*$)")[1].substring(1));
		day = Integer.parseInt(getClass().getSimpleName().split("Day(?=\\d+$)")[1]);
	}

	public abstract String name();

	public abstract String part1(final Path input) throws IOException;

	public abstract String part2(final Path input) throws IOException;

	public final int run() {
		final String s = System.getProperty("perf");
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
		final Path input_folder = Path.of(String.format("src/main/resources/input/%d%s", Integer.valueOf(year),
				sample_prop == null ? "" : "_samples"));
		final Path input_path = input_folder.resolve(input + ".txt");
		Path answers_path = input_folder.resolve(input + "_answers.txt");
		if (!Files.isReadable(answers_path)) {
			answers_path = input_folder.resolve("answers_" + input + ".txt");
		}

		if (sample_prop != null) {
			System.out.format("--- %d Day %d: Working from sample data set '%s' ---%n", Integer.valueOf(year),
					Integer.valueOf(day), input);
			sample = true;
		}

		int score = 0;
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
			try {
				final long start = System.currentTimeMillis();
				final String answer = part1(input_path);
				final long duration = System.currentTimeMillis() - start;
				if (checkResult(1, answers, answer, duration)) {
					score++;
				}
			} catch (Exception e) {
				Logger.error(e, "Error: {}", e);
			}

			if (perf) {
				for (int i = 0; i < iterations; i++) {
					part2(input_path);
				}
			}
			final long start = System.currentTimeMillis();
			final String answer = part2(input_path);
			final long duration = System.currentTimeMillis() - start;
			if (checkResult(2, answers, answer, duration)) {
				score++;
			}
		} catch (IOException e) {
			System.out.println("Error unable to read input '" + input_path + "'");
		}

		return score;
	}

	public final boolean isSample() {
		return sample;
	}

	private boolean checkResult(int part, String[] answers, String result, long duration) {
		if (result != null && day == 25 && part == 2 && result.equals(NOT_APPLICABLE)
				|| answers != null && result != null && result.equals(answers[part - 1])) {
			System.out.format("%d Day %d: '%s' part %d - Correct answer: %s. Duration: %,dms%n", Integer.valueOf(year),
					Integer.valueOf(day), name(), Integer.valueOf(part), result, Long.valueOf(duration));

			return true;
		}

		if (answers == null || answers.length < part) {
			System.out.format("%d Day %d: '%s' part %d: %s. Duration: %dms%n", Integer.valueOf(year),
					Integer.valueOf(day), name(), Integer.valueOf(part), result, Long.valueOf(duration));

			return false;
		}

		System.out.format("WRONG! %d Day %d: '%s' part %d - Wrong answer (%s), expected: %s. Duration: %,dms%n",
				Integer.valueOf(year), Integer.valueOf(day), name(), Integer.valueOf(part), result, answers[part - 1],
				Long.valueOf(duration));

		return false;
	}
}
