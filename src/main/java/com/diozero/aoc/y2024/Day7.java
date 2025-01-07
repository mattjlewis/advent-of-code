package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.MathUtils;

public class Day7 extends Day {
	public static void main(String[] args) {
		new Day7().run();
	}

	@Override
	public String name() {
		return "Bridge Repair";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(Files.lines(input).map(Equation::load).filter(eq -> eq.canBeTrue(false, 0, eq.numbers[0]))
				.mapToLong(Equation::testValue).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Long.toString(Files.lines(input).map(Equation::load).filter(eq -> eq.canBeTrue(true, 0, eq.numbers[0]))
				.mapToLong(Equation::testValue).sum());
	}

	private static record Equation(long testValue, int[] numbers) {
		public static Equation load(String line) {
			final String[] parts = line.split(":");

			return new Equation(Long.parseLong(parts[0]),
					Arrays.stream(parts[1].trim().split(" ")).mapToInt(Integer::parseInt).toArray());
		}

		public boolean canBeTrue(boolean includeContentation, int index, long total) {
			final int next_index = index + 1;
			if (next_index == numbers.length) {
				return testValue == total;
			}

			if (includeContentation) {
				return canBeTrue(includeContentation, next_index, total + numbers[next_index])
						|| canBeTrue(includeContentation, next_index, total * numbers[next_index])
						|| canBeTrue(includeContentation, next_index, MathUtils.concat(total, numbers[next_index]));
			}

			return canBeTrue(includeContentation, next_index, total + numbers[next_index])
					|| canBeTrue(includeContentation, next_index, total * numbers[next_index]);
		}
	}
}
