package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day6 extends Day {
	public static void main(String[] args) {
		new Day6().run();
	}

	@Override
	public String name() {
		return "Tuning Trouble";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return findStartOfPacketMarker(Files.lines(input).findFirst().orElseThrow(), 4);
	}

	@Override
	public String part2(final Path input) throws IOException {
		return findStartOfPacketMarker(Files.lines(input).findFirst().orElseThrow(), 14);
	}

	private static String findStartOfPacketMarker(String s, int size) {
		return Integer.toString(IntStream.range(0, s.length() - size)
				.takeWhile(i -> s.substring(i, i + size).chars().distinct().count() != size).max().orElseThrow() + size
				+ 1);
	}
}
