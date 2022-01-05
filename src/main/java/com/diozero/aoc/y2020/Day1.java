package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day1 extends AocBase {
	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		final int[] values = loadIntegerArray(input);
		Logger.debug(Arrays.toString(values));

		final int target = 2020;
		int[] numbers = { 0, 0 };
		for (int i = 0; i < values.length - 1; i++) {
			for (int j = i; j < values.length; j++) {
				if (values[i] + values[j] == target) {
					numbers = new int[] { values[i], values[j] };
				}
			}
		}

		return numbers[0] * numbers[1];
	}

	@Override
	public long part2(Path input) throws IOException {
		final int[] values = loadIntegerArray(input);
		Logger.debug(Arrays.toString(values));

		final int target = 2020;
		int[] numbers = { 0, 0, 0 };
		for (int i = 0; i < values.length - 1; i++) {
			for (int j = i; j < values.length; j++) {
				for (int k = j; k < values.length; k++) {
					if (values[i] + values[j] + values[k] == target) {
						numbers = new int[] { values[i], values[j], values[k] };
					}
				}
			}
		}

		return numbers[0] * numbers[1] * numbers[2];
	}
}
