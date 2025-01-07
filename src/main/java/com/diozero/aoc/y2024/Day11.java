package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.MathUtils;

public class Day11 extends Day {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public String name() {
		return "Plutonian Pebbles";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(calculate(input, 25));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(calculate(input, 75));
	}

	private static long calculate(final Path input, int numBlinks) throws IOException {
		Map<Long, Long> stones = Arrays.stream(Files.readAllLines(input).get(0).split(" ")).map(Long::valueOf)
				.collect(Collectors.toMap(Function.identity(), l -> Long.valueOf(1)));

		for (int count = 0; count < numBlinks; count++) {
			final Map<Long, Long> next_stones = new HashMap<>();
			for (Map.Entry<Long, Long> entry : stones.entrySet()) {
				for (long stone : blink(entry.getKey().longValue())) {
					next_stones.merge(Long.valueOf(stone), entry.getValue(), Long::sum);
				}
			}
			stones = next_stones;
		}

		return stones.values().stream().mapToLong(Long::longValue).sum();
	}

	private static long[] blink(long stone) {
		final int num_digits = MathUtils.countDigits(stone);
		if (stone == 0) {
			return new long[] { 1 };
		}
		if (num_digits % 2 == 0) {
			return MathUtils.split(stone);
		}
		return new long[] { stone * 2024 };
	}
}
