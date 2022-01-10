package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day7 extends AocBase {
	public static void main(String[] args) {
		new Day7().run();
	}

	private static int[] loadData(Path input) throws IOException {
		return Arrays.stream(Files.lines(input).findFirst().map(line -> line.split(",")).orElseThrow())
				.mapToInt(Integer::parseInt).sorted().toArray();
	}

	// distance: 333, fuel: 328262
	@Override
	public String part1(Path input) throws IOException {
		final int[] positions = loadData(input);

		// Get the median value
		final int median = positions[positions.length / 2];

		final int fuel = Arrays.stream(positions).map(pos -> Math.abs(pos - median)).sum();
		Logger.debug("distance: {}, fuel: {}", median, fuel);
		return Integer.toString(fuel);
	}

	// distance: 464, min fuel: 90040997
	@Override
	public String part2(Path input) throws IOException {
		final int[] positions = loadData(input);

		// Old school loop to get min, max, sum and mean of positions - avoiding
		// multiple streams
		int sum = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < positions.length; i++) {
			if (positions[i] < min) {
				min = positions[i];
			}
			if (positions[i] > max) {
				max = positions[i];
			}
			sum += positions[i];
		}

		// Use the mean distance as an approximate starting point
		final int mean = (int) Math.floor(sum / (double) positions.length);
		Logger.debug("mean: {}, min: {}, max: {}", mean, min, max);

		/*-
		 * Movement cost by distance
		 * 1 = 1  = (1 * (1 + 1)) / 2
		 * 2 = 3  = (2 * (2 + 1)) / 2
		 * 3 = 6  = (3 * (3 + 1)) / 2
		 * 4 = 10 = (4 * (4 + 1)) / 2
		 * 5 = 15
		 * 6 = 21
		 * 7 = 28
		 * 8 = 36
		 * 9 = 45
		 *10 = 55
		 *11 = 66
		 */

		int min_fuel = Integer.MAX_VALUE;
		int distance = -1;
		// Loop until the fuel stops decreasing
		for (int x = Math.max(0, mean - 10); x < Math.min(max, mean + 10); x++) {
			int fuel = 0;
			for (int i = 0; i < positions.length; i++) {
				int dist = Math.abs(positions[i] - x);
				fuel += (dist * (dist + 1)) / 2;
			}
			Logger.debug("fuel at {}: {}", x, fuel);
			if (fuel < min_fuel) {
				min_fuel = fuel;
				distance = x;
			} else {
				Logger.debug("Fuel is increasing, exiting loop");
				break;
			}
		}

		Logger.debug("distance: {}, min fuel: {}", distance, min_fuel);
		return Integer.toString(min_fuel);
	}
}
