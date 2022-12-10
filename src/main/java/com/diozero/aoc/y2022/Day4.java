package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.diozero.aoc.Day;

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

	private static Assignment[] load(String line) {
		return Stream.of(line.split(",")).map(Assignment::parse).toArray(Assignment[]::new);
	}

	private static boolean fullyContains(Assignment[] assignmentPair) {
		return assignmentPair[0].fullyContains(assignmentPair[1]) || assignmentPair[1].fullyContains(assignmentPair[0]);
	}

	private static boolean overlaps(Assignment[] assignmentPair) {
		return assignmentPair[0].overlaps(assignmentPair[1]);
	}

	private static record Assignment(int start, int end) {
		public static Assignment parse(String startEnd) {
			final String[] parts = startEnd.split("-");
			return new Assignment(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		}

		public boolean fullyContains(Assignment other) {
			return start <= other.start && end >= other.end;
		}

		public boolean overlaps(Assignment other) {
			return start <= other.end && end >= other.start;
		}
	}
}
