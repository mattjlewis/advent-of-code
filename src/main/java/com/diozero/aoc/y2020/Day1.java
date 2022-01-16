package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day1 extends Day {
	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public String name() {
		return "Report Repair";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int[] values = TextParser.loadIntArray(input);
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

		return Long.toString(numbers[0] * numbers[1]);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int[] values = TextParser.loadIntArray(input);
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

		return Long.toString(numbers[0] * numbers[1] * numbers[2]);
	}
}
