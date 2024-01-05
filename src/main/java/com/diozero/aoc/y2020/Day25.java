package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Path;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day25 extends Day {
	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String name() {
		return "Combo Breaker";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final long[] public_keys = TextParser.loadLongArray(input);

		final long card_pk = public_keys[0];
		final long door_pk = public_keys[1];

		Logger.debug("door_pk: {}", card_pk);

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
		Logger.debug("card loop_size: {}", loop_size);

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
		return Day.NOT_APPLICABLE;
	}
}
