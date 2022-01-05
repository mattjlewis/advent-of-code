package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.diozero.aoc.AocBase;

public class Day5 extends AocBase {
	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		return Files.lines(input).mapToInt(Day5::getSeatId).max().orElseThrow();
	}

	@Override
	public long part2(Path input) throws IOException {
		int[] populated_seat_ids = Files.lines(input).mapToInt(Day5::getSeatId).sorted().toArray();

		int my_seat_id = -1;
		// Find the gap in seat ids
		for (int i = 1; i < populated_seat_ids.length; i++) {
			if (populated_seat_ids[i - 1] != populated_seat_ids[i] - 1) {
				my_seat_id = populated_seat_ids[i] - 1;
				break;
			}
		}

		return my_seat_id;
	}

	private static final int getSeatId(String line) {
		char[] chars = line.toCharArray();

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
