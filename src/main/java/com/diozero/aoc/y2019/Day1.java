package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day1 extends Day {
	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public String name() {
		return "The Tyranny of the Rocket Equation";
	}

	@Override
	public String part1(Path input) throws IOException {
		return Integer.toString(Arrays.stream(TextParser.loadIntArray(input)).map(mass -> mass / 3 - 2).sum());
	}

	@Override
	public String part2(Path input) throws IOException {
		final int[] masses = TextParser.loadIntArray(input);

		int total_fuel = 0;
		for (int mass : masses) {
			int fuel = mass;
			while (true) {
				fuel = fuel / 3 - 2;
				if (fuel <= 0) {
					break;
				}
				total_fuel += fuel;
			}
		}

		return Integer.toString(total_fuel);
	}
}
