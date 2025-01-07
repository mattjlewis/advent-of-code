package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day1 extends Day {
	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public String name() {
		return "Historian Hysteria";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final IntStream left_stream = Files.lines(input).mapToInt(line -> Integer.parseInt(line.split("\\s+")[0]))
				.sorted();
		final OfInt right_it = Files.lines(input).mapToInt(line -> Integer.parseInt(line.split("\\s+")[1])).sorted()
				.iterator();

		return Integer.toString(left_stream.map(i -> Math.abs(i - right_it.nextInt())).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final IntStream left_stream = Files.lines(input).mapToInt(line -> Integer.parseInt(line.split("\\s+")[0]))
				.sorted();
		final Map<Integer, Long> right_counts = Files.lines(input).map(line -> Integer.valueOf(line.split("\\s+")[1]))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		return Long.toString(left_stream
				.mapToLong(i -> i * right_counts.getOrDefault(Integer.valueOf(i), Long.valueOf(0)).longValue()).sum());
	}
}
