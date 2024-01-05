package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;

public class Day15 extends Day {
	private static final Pattern HASHMAP_PATTERN = Pattern.compile("(\\w+)([=-])(\\d*)");

	public static void main(String[] args) {
		new Day15().run();
	}

	@Override
	public String name() {
		return "Lens Library";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Arrays.stream(loadSteps(input)).mapToInt(Day15::hash).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Map<Integer, LinkedHashMap<String, Integer>> boxes = new HashMap<>();

		for (String step : Files.readString(input).split(",")) {
			final Matcher m = HASHMAP_PATTERN.matcher(step);
			if (!m.matches()) {
				throw new IllegalArgumentException(step);
			}
			final String lens_label = m.group(1);
			final Integer lens_hash = Integer.valueOf(hash(lens_label));
			final Operation op = Operation.of(m.group(2));
			if (op == Operation.ADD) {
				boxes.computeIfAbsent(lens_hash, i -> new LinkedHashMap<>()).put(lens_label,
						Integer.valueOf(m.group(3)));
			} else {
				if (boxes.containsKey(lens_hash)) {
					boxes.get(lens_hash).remove(lens_label);
				}
			}
		}

		return Integer.toString(boxes.entrySet().stream()
				.mapToInt(entry -> getFocussingPower(entry.getKey().intValue() + 1, entry.getValue().values())).sum());
	}

	private static int getFocussingPower(int boxNumber, Collection<Integer> focalLengths) {
		final AtomicInteger slot = new AtomicInteger(0);
		return focalLengths.stream()
				.mapToInt(focal_length -> boxNumber * slot.incrementAndGet() * focal_length.intValue()).sum();
	}

	private static String[] loadSteps(Path input) throws IOException {
		return Files.readString(input).split(",");
	}

	private static int hash(String s) {
		return s.chars().reduce(0, (i, ch) -> ((i + ch) * 17) % 256);
	}

	private static enum Operation {
		ADD, REMOVE;

		public static Operation of(String op) {
			return switch (op) {
			case "=" -> ADD;
			case "-" -> REMOVE;
			default -> throw new IllegalArgumentException("Invalid operation '" + op + "'");
			};
		}
	}
}
