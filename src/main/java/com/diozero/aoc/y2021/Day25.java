package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.PrintUtil;

public class Day25 extends Day {
	private static final char EAST = '>';
	private static final char SOUTH = 'v';
	private static final char SPACE = '.';

	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String name() {
		return "Sea Cucumber";
	}

	@Override
	public String part1(Path input) throws IOException {
		char[][] cucumbers = Files.lines(input).map(String::toCharArray).toArray(char[][]::new);
		int width = cucumbers[0].length;
		int height = cucumbers.length;

		if (Logger.isDebugEnabled()) {
			System.out.println("Initial state:");
			PrintUtil.print(cucumbers);
			System.out.println();
		}

		/*
		 * When a herd moves forward, every sea cucumber in the herd first
		 * simultaneously considers whether there is a sea cucumber in the adjacent
		 * location it's facing (even another sea cucumber facing the same direction),
		 * and then every sea cucumber facing an empty location simultaneously moves
		 * into that location.
		 */

		boolean moved;
		int step = 0;
		do {
			moved = false;
			char[][] new_cucumbers = ArrayUtil.clone(cucumbers);

			// First move the east facing sea cucumbers
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int next = (x + 1) % width;
					if (cucumbers[y][x] == EAST && cucumbers[y][next] == SPACE) {
						new_cucumbers[y][next] = EAST;
						new_cucumbers[y][x] = SPACE;
						moved = true;
						// Increment to avoid moving the same cucumber more than once
						x++;
					} else {
						new_cucumbers[y][x] = cucumbers[y][x];
					}
				}
			}

			cucumbers = new_cucumbers;
			new_cucumbers = ArrayUtil.clone(cucumbers);

			// Then move the south facing sea cucumbers
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int next = (y + 1) % height;
					if (cucumbers[y][x] == SOUTH && cucumbers[next][x] == SPACE) {
						new_cucumbers[next][x] = SOUTH;
						new_cucumbers[y][x] = SPACE;
						moved = true;
						// Increment to avoid moving the same cucumber more than once
						y++;
					} else {
						new_cucumbers[y][x] = cucumbers[y][x];
					}
				}
			}

			cucumbers = new_cucumbers;
			step++;

			if (Logger.isDebugEnabled()) {
				System.out.println("After " + step + " step" + (step > 1 ? "s" : "") + ":");
				PrintUtil.print(cucumbers);
				System.out.println();
			}
		} while (moved);

		return Integer.toString(step);
	}

	@Override
	public String part2(Path input) throws IOException {
		return "";
	}
}
