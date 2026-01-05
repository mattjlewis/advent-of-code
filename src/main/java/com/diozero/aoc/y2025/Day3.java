package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.diozero.aoc.Day;

public class Day3 extends Day {
	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public String name() {
		return "Lobby";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(puzzleInput(input).mapToLong(bank -> maxJoltage(bank, 2)).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(puzzleInput(input).mapToLong(bank -> maxJoltage(bank, 12)).sum());
	}

	private static Stream<int[]> puzzleInput(final Path input) throws IOException {
		return Files.lines(input).map(s -> s.chars().map(Character::getNumericValue).toArray());
	}

	private static final long maxJoltage(int[] bank, int numDigits) {
		return IntStream.range(0, numDigits).boxed()
				.gather(Gatherers.scan(() -> Integer.valueOf(-1),
						(position, digit) -> getNextIndex(bank, numDigits, position, digit)))
				.mapToLong(i -> bank[i.intValue()]).reduce(0L, (i, j) -> i * 10 + j);
	}

	private static Integer getNextIndex(int[] bank, int numDigits, Integer position, Integer digit) {
		// Find the index of the largest number from pos+1 to bank.length - (numDigits-digit-1)
		return Integer
				.valueOf(getMaxIndex(bank, position.intValue() + 1, bank.length - (numDigits - digit.intValue() - 1)));
	}

	private static int getMaxIndex(int[] bank, int start, int end) {
		// Find the first index of the maximum value between array indices start and end
		return IntStream.range(start, end).reduce((i, j) -> bank[j] > bank[i] ? j : i).getAsInt();
	}
}
