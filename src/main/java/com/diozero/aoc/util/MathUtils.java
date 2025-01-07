package com.diozero.aoc.util;

public class MathUtils {
	public static int countDigits(int num) {
		if (num == 0) {
			return 1;
		}
		return (int) Math.floor(Math.log10(num)) + 1;
	}

	public static int countDigits(long num) {
		if (num == 0) {
			return 1;
		}
		return (int) Math.floor(Math.log10(num)) + 1;
	}

	public static int concat(int i1, int i2) {
		// Using log10 and pow10 is faster than string concatenation & parsing - 90ms vs 255ms
		// return Long.parseLong(l1 + Long.toString(l2));
		return i1 * (int) Math.round(Math.pow(10, countDigits(i2))) + i2;
	}

	public static long concat(long l1, long l2) {
		// Using log10 and pow10 is faster than string concatenation & parsing - 90ms vs 255ms
		// return Long.parseLong(l1 + Long.toString(l2));
		return l1 * Math.round(Math.pow(10, countDigits(l2))) + l2;
	}

	public static long[] split(long l, int position) {
		return splitInternal(l, countDigits(l), position);
	}

	public static long[] split(long l) {
		final int num_digits = countDigits(l);
		return splitInternal(l, num_digits, num_digits / 2);
	}

	private static long[] splitInternal(long l, int numDigits, int position) {
		final long pow = (long) Math.pow(10, numDigits - position);
		return new long[] { l / pow, l % pow };
	}
}
