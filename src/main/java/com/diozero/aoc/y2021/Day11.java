package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.TextParser;

public class Day11 extends Day {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public String name() {
		return "Dumbo Octopus";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int[][] energy_levels = TextParser.loadIntMatrix(input);

		final int steps = 100;

		int num_flashes = 0;
		for (int i = 0; i < steps; i++) {
			num_flashes += incrementGrid(energy_levels);
			printGrid(i + 1, energy_levels);
		}

		return Integer.toString(num_flashes);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int[][] energy_levels = TextParser.loadIntMatrix(input);

		final int grid_size = energy_levels.length * energy_levels[0].length;
		int step = 0;
		while (true) {
			final int num_flashes = incrementGrid(energy_levels);
			printGrid(step + 1, energy_levels);
			step++;

			if (num_flashes == grid_size) {
				break;
			}
		}

		return Integer.toString(step);
	}

	private static int incrementGrid(final int[][] energyLevels) {
		// First of all increment all energy levels
		for (int y = 0; y < energyLevels.length; y++) {
			for (int x = 0; x < energyLevels[y].length; x++) {
				energyLevels[y][x]++;
			}
		}

		final boolean[][] radiated = new boolean[energyLevels.length][energyLevels[0].length];
		for (int y = 0; y < energyLevels.length;) {
			for (int x = 0; x < energyLevels[y].length;) {
				if (energyLevels[y][x] >= 10 && !radiated[y][x]) {
					for (int dy = Math.max(0, y - 1); dy <= Math.min(energyLevels.length - 1, y + 1); dy++) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(energyLevels[dy].length - 1, x + 1); dx++) {
							if (x != dx || y != dy) {
								energyLevels[dy][dx]++;
							}
						}
					}
					radiated[y][x] = true;
					x = Math.max(0, x - 1);
					y = Math.max(0, y - 1);
				} else {
					x++;
				}
			}
			y++;
		}

		int num_flashes = 0;
		for (int y = 0; y < energyLevels.length; y++) {
			for (int x = 0; x < energyLevels[y].length; x++) {
				if (energyLevels[y][x] > 9) {
					num_flashes++;
					energyLevels[y][x] = 0;
				}
			}
		}

		return num_flashes;
	}

	private static void printGrid(int step, int[][] energyLevels) {
		if (!Logger.isDebugEnabled()) {
			return;
		}

		if (step == 0) {
			System.out.println("Before any steps:");
		} else {
			System.out.println("After step " + step + ":");
		}

		ArrayUtil.print(energyLevels);
	}
}
