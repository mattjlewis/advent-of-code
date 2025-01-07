package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;

public class Day3 extends Day {
	private final static Pattern PATTERN = Pattern.compile("(mul\\((\\d+),(\\d+)\\))|(do\\(\\)|don't\\(\\))");
	private static final AtomicBoolean TRUE = new AtomicBoolean(true);

	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public String name() {
		return "Mull It Over";
	}

	private static final int process(MatchResult result, Optional<AtomicBoolean> enabled) {
		// Match group 1 is the "mul" instruction which is null if a do / don't instruction
		if (result.group(1) == null) {
			// Match group 4 contains the "do()" / "don't()" instruction
			enabled.ifPresent(e -> e.set(result.group(4).equals("do()")));

			return 0;
		}

		if (!enabled.orElse(TRUE).get()) {
			return 0;
		}

		return Integer.parseInt(result.group(2)) * Integer.parseInt(result.group(3));
	}

	private static final int sum(String line, Optional<AtomicBoolean> enabled) {
		return PATTERN.matcher(line).results().mapToInt(r -> process(r, enabled)).sum();
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).mapToInt(line -> sum(line, Optional.empty())).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Optional<AtomicBoolean> enabled = Optional.of(new AtomicBoolean(true));
		return Long.toString(Files.lines(input).mapToLong(line -> sum(line, enabled)).sum());
	}
}
