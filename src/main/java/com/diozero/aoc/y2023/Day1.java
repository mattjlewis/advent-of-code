package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.diozero.aoc.Day;

public class Day1 extends Day {
	// The minimum overlap with other written numbers
	private static final Map<String, String> NUMBERS = Map.of( //
			"one", "o1e", //
			"two", "t2o", //
			"three", "t3e", //
			"four", "4", //
			"five", "5e", //
			"six", "6", //
			"seven", "7n", //
			"eight", "e8t", //
			"nine", "n9e");

	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public String name() {
		return "Trebuchet?!";
	}

	private static int firstLastDigits(String line) {
		int[] digits = line.chars().filter(Character::isDigit).map(ch -> ch - '0').toArray();
		return digits[0] * 10 + digits[digits.length - 1];
	}

	private static String replaceNumbers(String s) {
		String result = s;
		for (Map.Entry<String, String> number : NUMBERS.entrySet()) {
			result = result.replace(number.getKey(), number.getValue());
		}
		return result;
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).mapToInt(Day1::firstLastDigits).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).map(Day1::replaceNumbers).mapToInt(Day1::firstLastDigits).sum());
	}
}
