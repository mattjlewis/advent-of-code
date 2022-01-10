package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Path;

import com.diozero.aoc.AocBase;

public class Day9 extends AocBase {
	public static void main(String[] args) {
		new Day9().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		long[] values = loadLongArray(input);

		int preamble = 25;
		if (input.toString().contains("samples")) {
			preamble = 5;
		}

		return Long.toString(getInvalidNumber(values, preamble));
	}

	@Override
	public String part2(Path input) throws IOException {
		long[] values = loadLongArray(input);

		int preamble = 25;
		if (input.toString().contains("samples")) {
			preamble = 5;
		}

		long invalid_number = getInvalidNumber(values, preamble);
		int invalid_number_index;
		for (invalid_number_index = 0; invalid_number_index < values.length
				&& values[invalid_number_index] != invalid_number; invalid_number_index++) {
			//
		}

		int i;
		int j = 0;
		a: for (i = 0; i < invalid_number_index - 1; i++) {
			long sum = values[i];
			for (j = i + 1; j < invalid_number_index && sum < invalid_number; j++) {
				sum += values[j];
				if (sum == invalid_number) {
					break a;
				}
			}
		}

		long min = Integer.MAX_VALUE;
		long max = Integer.MIN_VALUE;
		for (int x = i; x <= j; x++) {
			min = Math.min(min, values[x]);
			max = Math.max(max, values[x]);
		}

		return Long.toString(min + max);
	}

	private static long getInvalidNumber(long[] values, int preamble) {
		long invalid_number = -1;
		for (int i = preamble; i < values.length; i++) {
			if (!isValid(values, preamble, i)) {
				invalid_number = values[i];
				break;
			}
		}

		return invalid_number;
	}

	private static boolean isValid(long[] values, int preamble, int index) {
		// Find a pair of numbers that adds up to values[index];
		for (int i = index - preamble; i < index - 1; i++) {
			for (int j = i + 1; j < index; j++) {
				if (values[i] + values[j] == values[index]) {
					return true;
				}
			}
		}

		return false;
	}
}
