package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.LongRange;

public class Day5 extends Day {
	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public String name() {
		return "Cafeteria";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<String> lines = Files.readAllLines(input);
		final List<LongRange> ranges = lines.stream().takeWhile(line -> !line.isEmpty())
				.map(line -> LongRange.parse(line, true)).toList();

		return Long.toString(lines.stream().skip(ranges.size() + 1).mapToLong(Long::parseLong)
				.filter(l -> contains(ranges, l)).count());
	}

	private static boolean contains(List<LongRange> ranges, long l) {
		return ranges.stream().anyMatch(r -> r.contains(l));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<LongRange> ranges = Files.lines(input).takeWhile(line -> !line.isEmpty())
				.map(line -> LongRange.parse(line, true)).collect(Collectors.toList());

		LongRange.merge(ranges);

		return Long.toString(ranges.stream().mapToLong(LongRange::length).sum());
	}
}
