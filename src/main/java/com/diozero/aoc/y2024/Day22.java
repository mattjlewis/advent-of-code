package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.diozero.aoc.Day;

public class Day22 extends Day {
	public static void main(String[] args) {
		new Day22().run();
	}

	@Override
	public String name() {
		return "Monkey Market";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(Files.lines(input).mapToInt(Integer::parseInt).mapToLong(i -> cipher(i, 2000)).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(mostBananas(Files.lines(input).mapToInt(Integer::parseInt).toArray(), 2000));
	}

	private static long mostBananas(int[] secrets, int count) {
		final long[] result = new long[130321]; // 130321 = 19^4
		final int[] seen = new int[130321];

		int id = 0;
		for (final long secret : secrets) {
			final long first = cipher(secret);
			final long second = cipher(first);
			long current = cipher(second);
			Sequence sequence = new Sequence(0, diff(secret, first), diff(first, second), diff(second, current));

			for (int i = 3; i < count; i++) {
				final long previous = current;
				current = cipher(current);
				sequence = new Sequence(sequence.b, sequence.c, sequence.d, diff(previous, current));
				final int key = sequence.hashCode();

				if (seen[key] != id + 1) {
					result[key] += current % 10;
					seen[key] = id + 1;
				}
			}
			id++;
		}

		return Arrays.stream(result).max().orElseThrow();
	}

	private static long cipher(int number, int count) {
		long n = number;
		for (int i = 0; i < count; i++) {
			n = cipher(n);
		}
		return n;
	}

	private static long cipher(long number) {
		long n = number;
		n = (n ^ (n << 6)) & 0xffffff;
		n = (n ^ (n >> 5)) & 0xffffff;
		return (n ^ (n << 11)) & 0xffffff;
	}

	private static int diff(long previous, long current) {
		return (int) (9 + current % 10 - previous % 10); // from (-9 to 9) to (0 to 18)
	}

	private static record Sequence(int a, int b, int c, int d) {
		@Override
		public int hashCode() {
			return 6859 * a + 361 * b + 19 * c + d; // 6859 = 19^3, 361 = 19^2, 19 = 19^1
		}
	}
}
