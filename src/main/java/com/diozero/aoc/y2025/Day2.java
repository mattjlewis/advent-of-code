package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.ToLongFunction;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.LongRange;

public class Day2 extends Day {
	public static void main(String[] args) {
		new Day2().run();
	}

	@Override
	public String name() {
		return "Gift Shop";
	}

	private static long countInvalidIds(final Path input, ToLongFunction<LongRange> func) throws IOException {
		return Arrays.stream(Files.readString(input).split(",")).map(LongRange::parse).mapToLong(func).sum();
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(countInvalidIds(input, Day2::countPalindromes));
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(countInvalidIds(input, Day2::countRepeats));
	}

	public static long countPalindromes(LongRange range) {
		long palindromes = 0;

		for (long n = range.start(); n <= range.end(); n++) {
			final String sn = Long.toString(n);
			if (sn.substring(0, sn.length() / 2).equals(sn.substring(sn.length() / 2))) {
				palindromes += n;
			}
		}

		return palindromes;
	}

	public static long countRepeats(LongRange range) {
		long repeats = 0;

		for (long n = range.start(); n <= range.end(); n++) {
			final String sn = Long.toString(n);
			for (int i = 1; i <= sn.length() / 2; i++) {
				if (sn.length() % i != 0) {
					continue;
				}
				int j = 0;
				while (j < sn.length() && sn.substring(j, j + i).equals(sn.substring(0, i))) {
					j += i;
				}
				if (j == sn.length()) {
					repeats += n;
					break;
				}
			}
		}

		return repeats;
	}
}
