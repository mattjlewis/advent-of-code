package com.diozero.aoc.y2019;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.ArrayUtil;

/**
 * Credit:
 * https://github.com/akaritakai/AdventOfCode2019/blob/2e062194221f1832eb56b9e292f7cef1a657d57d/src/main/java/net/akaritakai/aoc2019/Puzzle22.java
 */
public class Day22 extends Day {
	private static final String CUT = "cut ";
	private static final String DEAL_WITH_INCREMENT = "deal with increment ";
	private static final String DEAL_INTO_NEW_STACK = "deal into new stack";

	public static void main(String[] args) {
		new Day22().run();
	}

	@Override
	public String name() {
		return "Slam Shuffle";
	}

	@Override
	public String part1(Path input) throws IOException {
		int range;
		if (input.toString().contains("samples")) {
			range = 10;
		} else {
			range = 10_007;
		}
		Deck deck = new Deck(IntStream.range(0, range).toArray());
		Files.lines(input).forEach(instr -> apply(instr, deck));

		if (range == 9) {
			return deck.toString();
		}

		return Integer.toString(deck.indexOf(2019));
	}

	@Override
	public String part2(Path input) throws IOException {
		List<String> instructions = Files.lines(input).collect(Collectors.toList());
		Collections.reverse(instructions);

		BigInteger deck_size = BigInteger.valueOf(119_315_717_514_047L);
		BigInteger position = BigInteger.valueOf(2020);
		BigInteger f1 = BigInteger.valueOf(inverseApply(instructions, deck_size.longValue(), position.longValue()));
		BigInteger f2 = BigInteger.valueOf(inverseApply(instructions, deck_size.longValue(), f1.longValue()));

		// f(i) = A*i + B
		BigInteger a = f1.subtract(f2)
				.multiply(position.subtract(f1).add(deck_size).modInverse(deck_size).mod(deck_size)).mod(deck_size);
		BigInteger b = f1.subtract(a.multiply(position)).mod(deck_size);

		BigInteger num_shuffles = BigInteger.valueOf(101_741_582_076_661L);

		BigInteger result = a.modPow(num_shuffles, deck_size).multiply(position).add(a.modPow(num_shuffles, deck_size)
				.subtract(BigInteger.ONE).multiply(a.subtract(BigInteger.ONE).modInverse(deck_size)).multiply(b))
				.mod(deck_size);

		return result.toString();
	}

	private static long inverseApply(Collection<String> instructions, long size, long i) {
		long val = i;

		for (String instruction : instructions) {
			if (instruction.equals(DEAL_INTO_NEW_STACK)) {
				val = reverse(size, val);
			} else if (instruction.startsWith(DEAL_WITH_INCREMENT)) {
				val = dealWithIncrement(size, val,
						Integer.parseInt(instruction.substring(DEAL_WITH_INCREMENT.length())));
			} else if (instruction.startsWith(CUT)) {
				val = cut(size, val, Integer.parseInt(instruction.substring(CUT.length())));
			}
		}

		return val;
	}

	private static long reverse(long size, long i) {
		return size - i - 1;
	}

	private static long dealWithIncrement(long size, long i, int n) {
		return BigInteger.valueOf(n).modInverse(BigInteger.valueOf(size)).multiply(BigInteger.valueOf(i))
				.mod(BigInteger.valueOf(size)).longValue();
	}

	private static long cut(long size, long i, long n) {
		return (i + n + size) % size;
	}

	private static void apply(String instruction, Deck deck) {
		if (instruction.equals(DEAL_INTO_NEW_STACK)) {
			deck.reverse();
		} else if (instruction.startsWith(DEAL_WITH_INCREMENT)) {
			deck.dealWithIncrement(Integer.parseInt(instruction.substring(DEAL_WITH_INCREMENT.length())));
		} else if (instruction.startsWith(CUT)) {
			deck.cut(Integer.parseInt(instruction.substring(CUT.length())));
		}
	}

	private static class Deck {
		private int[] array;

		public Deck(int[] array) {
			this.array = array;
		}

		public int indexOf(int val) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == val) {
					return i;
				}
			}

			return -1;
		}

		public void reverse() {
			ArrayUtil.reverse(array);
		}

		public void dealWithIncrement(int n) {
			int[] tmp = new int[array.length];
			for (int i = 0, j = 0; i < array.length; i++, j = (j + n) % array.length) {
				tmp[j] = array[i];
			}
			array = tmp;
		}

		public void cut(int n) {
			int[] tmp = new int[Math.abs(n)];
			if (n < 0) {
				// Copy bottom n numbers into tmp
				System.arraycopy(array, array.length + n, tmp, 0, -n);
				// Move all numbers in array right by n
				System.arraycopy(array, 0, array, -n, array.length + n);
				// Move what were the bottom n numbers into the top of n
				System.arraycopy(tmp, 0, array, 0, -n);
			} else {
				// Copy top n numbers into tmp
				System.arraycopy(array, 0, tmp, 0, n);
				// Move all numbers in array left by n
				System.arraycopy(array, n, array, 0, array.length - n);
				// Move what were the top n numbers into the bottom of n
				System.arraycopy(tmp, 0, array, array.length - n, n);
			}
		}

		@Override
		public String toString() {
			return Arrays.stream(array).mapToObj(Integer::toString).collect(Collectors.joining(" "));
		}
	}
}
