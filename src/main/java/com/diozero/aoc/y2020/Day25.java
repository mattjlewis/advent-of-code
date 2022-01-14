package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.diozero.aoc.AocBase;

public class Day25 extends AocBase {
	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String part1(Path input) throws IOException {
		long[] public_keys = Files.lines(input).mapToLong(Long::parseLong).toArray();

		long card_pk = public_keys[0];
		long door_pk = public_keys[1];

		System.out.println("door_pk: " + card_pk);

		final int div = 20201227;

		int loop_size = 0;
		long subject_number = 7;
		long value = 1;
		while (value != card_pk) {
			/*
			 * Set the value to itself multiplied by the subject number.
			 */
			value *= subject_number;
			/*
			 * Set the value to the remainder after dividing the value by 20201227.
			 */
			value %= div;

			loop_size++;
		}
		System.out.println("card loop_size: " + loop_size);

		subject_number = door_pk;
		value = 1;
		while (loop_size > 0) {
			value *= subject_number;
			value %= div;

			loop_size--;
		}

		return Long.toString(value);
	}

	@Override
	public String part2(Path input) throws IOException {
		return "";
	}
}
