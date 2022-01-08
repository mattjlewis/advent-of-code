package com.diozero.aoc.util;

import java.util.List;
import java.util.stream.IntStream;

/**
 * <p>
 * In mathematics, the
 * <a href= "https://en.wikipedia.org/wiki/Chinese_remainder_theorem">Chinese
 * remainder theorem</a> states that if one knows the remainders of the
 * Euclidean division of an integer n by several integers, then one can
 * determine uniquely the remainder of the division of n by the product of these
 * integers, under the condition that the divisors are pairwise coprime.
 * </p>
 *
 * <a href="https://rosettacode.org/wiki/Chinese_remainder_theorem#Java">Java
 * implementation</a> courtesy of Rosetta Code.
 */
public final class ChineseRemainderTheorem {
	private ChineseRemainderTheorem() {
	}

	public static long chineseRemainder(List<Integer> n, List<Integer> a) {
		return chineseRemainder(n.stream().mapToInt(Integer::intValue).toArray(),
				a.stream().mapToInt(Integer::intValue).toArray());
	}

	public static long chineseRemainder(int[] n, int[] a) {
		long product = IntStream.of(n).mapToLong(i -> i).reduce(1, (i, j) -> i * j);

		long sum = 0;
		for (int i = 0; i < n.length; i++) {
			long pp = product / n[i];
			sum += a[i] * modInverse(pp, n[i]) * pp;
		}

		return sum % product;
	}

	private static long modInverse(long a, long b) {
		if (b == 1) {
			return 0;
		}

		long aa = a;
		long bb = b;
		long x = 0;
		long y = 1;
		long temp;

		while (aa > 1) {
			// Quotient
			long q = aa / bb;

			temp = bb;
			bb = aa % bb;
			aa = temp;

			temp = x;
			x = y - q * x;
			y = temp;
		}

		// Make y positive
		if (y < 0) {
			y += b;
		}

		return y;
	}
}
