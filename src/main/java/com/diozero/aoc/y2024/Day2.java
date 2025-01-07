package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Path;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day2 extends Day {
	public static void main(String[] args) {
		new Day2().run();
	}

	@Override
	public String name() {
		return "Red-Nosed Reports";
	}

	private static boolean safe(int[] report) {
		int i = 1;
		int delta = Math.abs(report[i] - report[i - 1]);
		boolean increasing = report[1] > report[0];
		boolean safe = delta > 0 && delta <= 3;
		while (safe && i < report.length) {
			boolean next_increasing = report[i] > report[i - 1];
			delta = Math.abs(report[i] - report[i - 1]);
			safe = next_increasing == increasing && delta > 0 && delta <= 3;
			i++;
		}

		return safe;
	}

	@Override
	public String part1(final Path input) throws IOException {
		final int[][] matrix = TextParser.loadIntMatrix(input, true);
		// A report only counts as safe if both of the following are true:
		// * The levels are either all increasing or all decreasing.
		// * Any two adjacent levels differ by at least one and at most three.
		int safe_count = 0;
		for (int[] report : matrix) {
			if (safe(report)) {
				safe_count++;
			}
		}

		return Integer.toString(safe_count);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int[][] matrix = TextParser.loadIntMatrix(input, true);
		// A report only counts as safe if both of the following are true:
		// * The levels are either all increasing or all decreasing.
		// * Any two adjacent levels differ by at least one and at most three.
		// The Problem Dampener lets the reactor tolerate a single bad level
		int safe_count = 0;
		for (int[] report : matrix) {
			if (safe(report)) {
				safe_count++;
			} else {
				// Safe to initialise outside of the loop and reuse as all contents are overwritten
				int[] r = new int[report.length - 1];
				// Try removing each number to see if that makes the report safe
				for (int i = 0; i < report.length; i++) {
					if (i > 0) {
						// Add up to i
						System.arraycopy(report, 0, r, 0, i);
					}
					if (i < report.length - 1) {
						// Add from i+1
						System.arraycopy(report, i + 1, r, i, report.length - i - 1);
					}
					if (safe(r)) {
						safe_count++;
						break;
					}
				}
			}
		}

		return Integer.toString(safe_count);
	}
}
