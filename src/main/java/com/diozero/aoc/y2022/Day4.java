package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.IntRange;

public class Day4 extends Day {
	public static void main(String[] args) {
		new Day4().run();
	}

	@Override
	public String name() {
		return "Camp Cleanup";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(Files.lines(input).map(Day4::load).filter(Day4::fullyContains).count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(Files.lines(input).map(Day4::load).filter(Day4::overlaps).count());
	}

	private static IntRange[] load(String line) {
		return Arrays.stream(line.split(",")).map(IntRange::parseDashSeparated).toArray(IntRange[]::new);
	}

	private static boolean fullyContains(IntRange[] assignmentPair) {
		return assignmentPair[0].contains(assignmentPair[1]) || assignmentPair[1].contains(assignmentPair[0]);
	}

	private static boolean overlaps(IntRange[] assignmentPair) {
		return assignmentPair[0].overlaps(assignmentPair[1]);
	}
}
