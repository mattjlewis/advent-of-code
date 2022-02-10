package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day4 extends Day {
	public static void main(String[] args) {
		new Day4().run();
	}

	@Override
	public String name() {
		return "Secure Container";
	}

	@Override
	public String part1(Path input) throws IOException {
		int[] range = Files.lines(input).flatMap(line -> Arrays.stream(line.split("-"))).mapToInt(Integer::parseInt)
				.toArray();

		return Integer.toString((int) IntStream.rangeClosed(range[0], range[1]).filter(Day4::isValidPart1).count());
	}

	@Override
	public String part2(Path input) throws IOException {
		int[] range = Files.lines(input).flatMap(line -> Arrays.stream(line.split("-"))).mapToInt(Integer::parseInt)
				.toArray();

		return Integer.toString((int) IntStream.rangeClosed(range[0], range[1]).filter(Day4::isValidPart2).count());
	}

	private static boolean isValidPart1(int i) {
		/*-
		 * Password rules:
		 * - It is a six-digit number.
		 * - The value is within the range given in your puzzle input.
		 * - Two adjacent digits are the same (like 22 in 122345).
		 * - Going from left to right, the digits never decrease; they only ever increase or stay the same (like 111123
		 *   or 135679)
		 */
		final String s = Integer.toString(i);
		if (s.length() != 6) {
			return false;
		}

		int index = 1;
		boolean double_digits = false;
		do {
			char last_ch = s.charAt(index - 1);
			char ch = s.charAt(index++);
			if (ch < last_ch) {
				return false;
			}
			double_digits |= ch == last_ch;
		} while (index < s.length());

		return double_digits;
	}

	private static boolean isValidPart2(int i) {
		/*-
		 * Password rules:
		 * - It is a six-digit number.
		 * - The value is within the range given in your puzzle input.
		 * - Two adjacent digits are the same (like 22 in 122345).
		 * - Going from left to right, the digits never decrease; they only ever increase or stay the same (like 111123
		 *   or 135679)
		 */
		final String s = Integer.toString(i);
		if (s.length() != 6) {
			return false;
		}

		int index = 0;
		final Map<Character, AtomicInteger> digit_counts = new HashMap<>();
		do {
			char ch = s.charAt(index);
			if (index > 0) {
				if (ch < s.charAt(index - 1)) {
					return false;
				}
			}
			digit_counts.computeIfAbsent(Character.valueOf(ch), c -> new AtomicInteger()).incrementAndGet();
			index++;
		} while (index < s.length());

		// Identical to part 1 if change to ai.get() >= 2
		return digit_counts.values().stream().anyMatch(ai -> ai.get() == 2);
	}
}
