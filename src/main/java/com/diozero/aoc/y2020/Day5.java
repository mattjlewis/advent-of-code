package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.diozero.aoc.Day;

public class Day5 extends Day {
	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public String name() {
		return "Binary Boarding";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(Files.lines(input).mapToInt(Day5::getSeatId).max().orElseThrow());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int[] populated_seat_ids = Files.lines(input).mapToInt(Day5::getSeatId).sorted().toArray();

		int my_seat_id = -1;
		// Find the gap in seat ids
		for (int i = 1; i < populated_seat_ids.length; i++) {
			if (populated_seat_ids[i - 1] != populated_seat_ids[i] - 1) {
				my_seat_id = populated_seat_ids[i] - 1;
				break;
			}
		}

		return Integer.toString(my_seat_id);
	}

	private static final int getSeatId(final String line) {
		final char[] chars = line.toCharArray();

		// Process the F and Bs to get the row number
		int start = 0;
		int end = 128;
		for (int i = 0; i < 7; i++) {
			if (chars[i] == 'F') {
				end -= (end - start) / 2;
			} else {
				start += (end - start) / 2;
			}
		}
		int row = start;

		// Process the L and Rs to get the column number
		start = 0;
		end = 8;
		for (int i = 7; i < line.length(); i++) {
			if (chars[i] == 'L') {
				end -= (end - start) / 2;
			} else {
				start += (end - start) / 2;
			}
		}
		int column = start;

		return row * 8 + column;
	}
}
