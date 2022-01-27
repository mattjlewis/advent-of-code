package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day15 extends Day {
	public static void main(String[] args) {
		new Day15().run();
	}

	@Override
	public String name() {
		return "Rambunctious Recitation";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int[] starting_numbers = Arrays.stream(Files.lines(input).findFirst().orElseThrow().split(","))
				.mapToInt(Integer::parseInt).toArray();
		return Integer.toString(computeNth(starting_numbers, 2020));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int[] starting_numbers = Arrays.stream(Files.lines(input).findFirst().orElseThrow().split(","))
				.mapToInt(Integer::parseInt).toArray();
		return Integer.toString(computeNth(starting_numbers, 30_000_000));
	}

	private static final int computeNth(final int[] startingNumbers, final int iterations) {
		// Map from number to the turn it was last spoken on
		final Map<Integer, Integer> spoken_numbers = IntStream.range(0, startingNumbers.length - 1).boxed()
				.collect(Collectors.toMap(i -> Integer.valueOf(startingNumbers[i.intValue()]),
						i -> Integer.valueOf(i.intValue() + 1)));

		Integer current = Integer.valueOf(startingNumbers[startingNumbers.length - 1]);
		for (int i = startingNumbers.length; i < iterations; i++) {
			int next = spoken_numbers.containsKey(current) ? i - spoken_numbers.get(current).intValue() : 0;
			spoken_numbers.put(current, Integer.valueOf(i));
			current = Integer.valueOf(next);
		}

		return current.intValue();
	}
}
