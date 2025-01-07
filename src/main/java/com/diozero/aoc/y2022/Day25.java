package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.diozero.aoc.Day;

public class Day25 extends Day {
	private static final char[] SNAFU_DIGITS = "012=-".toCharArray();

	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String name() {
		return "Full of Hot Air";
	}

	@Override
	public String part1(Path input) throws IOException {
		return toSnafu(Files.lines(input).mapToLong(Day25::fromSnafu).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		return Day.NOT_APPLICABLE;
	}

	private static long fromSnafu(String s) {
		long l = 0;
		for (char ch : s.toCharArray()) {
			l *= 5;
			l += switch (ch) {
			case '2' -> 2;
			case '1' -> 1;
			case '0' -> 0;
			case '-' -> -1;
			case '=' -> -2;
			default -> throw new IllegalArgumentException("Invalid character '" + ch + "' in '" + s + "'");
			};
		}

		return l;
	}

	private static String toSnafu(final long l) {
		String s = "";
		long n = l;
		do {
			s = SNAFU_DIGITS[(int) (n % 5)] + s;
			n -= (n + 2) % 5 - 2;
			n /= 5;
		} while (n > 0);

		return s;
	}
}
