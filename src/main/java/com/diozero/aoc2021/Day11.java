package com.diozero.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc2021.util.AocBase;

public class Day11 extends AocBase {
	public static void main(String[] args) {
		new Day11().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		final int[][] energy_levels = loadData(input);
		printGrid(0, energy_levels);

		final int steps = 100;

		int num_flashes = 0;
		for (int i = 0; i < steps; i++) {
			num_flashes += incrementGrid(energy_levels);
			printGrid(i + 1, energy_levels);
		}

		return num_flashes;
	}

	@Override
	public long part2(Path input) throws IOException {
		final int[][] energy_levels = loadData(input);
		printGrid(0, energy_levels);

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

		return step;
	}

	private static int[][] loadData(Path input) throws IOException {
		return Files.lines(input).map(line -> line.chars().map(c -> c - 48).toArray()).toArray(int[][]::new);
	}

	private static int incrementGrid(int[][] energyLevels) {
		// First of all increment all energy levels
		for (int y = 0; y < energyLevels.length; y++) {
			for (int x = 0; x < energyLevels[y].length; x++) {
				energyLevels[y][x]++;
			}
		}
		// printGrid(energyLevels);

		final boolean[][] radiated = new boolean[energyLevels.length][energyLevels[0].length];
		for (int y = 0; y < energyLevels.length;) {
			for (int x = 0; x < energyLevels[y].length;) {
				if (energyLevels[y][x] >= 10 && !radiated[y][x]) {
					// Logger.debug("x: " + x + ", y: " + y);
					for (int dy = Math.max(0, y - 1); dy <= Math.min(energyLevels.length - 1, y + 1); dy++) {
						for (int dx = Math.max(0, x - 1); dx <= Math.min(energyLevels.length - 1, x + 1); dx++) {
							if (x != dx || y != dy) {
								// Logger.debug("dx: " + dx + ", dy: " + dy);
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
		// printGrid(energyLevels);

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

		for (int y = 0; y < energyLevels.length; y++) {
			for (int x = 0; x < energyLevels[y].length; x++) {
				System.out.print(energyLevels[y][x]);
			}
			System.out.println();
		}
		System.out.println();
	}

	static void printGrid(int[][] energyLevels) {
		if (!Logger.isDebugEnabled()) {
			return;
		}
		for (int y = 0; y < energyLevels.length; y++) {
			for (int x = 0; x < energyLevels[y].length; x++) {
				System.out.format("%3d", energyLevels[y][x]);
			}
			System.out.println();
		}
		System.out.println();
	}
}
