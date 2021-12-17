package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.tinylog.Logger;

import com.diozero.aoc.AocBase;

public class Day3 extends AocBase {
	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		// Get the number of bits in a line
		final int[] bit_counts = new int[Files.lines(input).findFirst().orElseThrow().length()];
		final AtomicInteger num_lines = new AtomicInteger();

		// Note it would be much easier if the matrix could be flipped so that the
		// number of 1s in each line could be counted via stream functions

		// Count the number of 1s in each position in all lines
		Files.lines(input).forEach(line -> {
			char[] chars = line.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				if (chars[i] == '1') {
					bit_counts[chars.length - i - 1]++;
				}
			}
			num_lines.incrementAndGet();
		});
		Logger.debug(Arrays.toString(bit_counts));

		int gamma = 0;
		for (int i = 0; i < bit_counts.length; i++) {
			Logger.debug("bits[{}]: {} ({})", i, bit_counts[i], (bit_counts[i] > num_lines.get() / 2) ? 1 : 0);
			gamma |= (bit_counts[i] > num_lines.get() / 2) ? (1 << i) : 0;
		}
		int epsilon = ~gamma & ((1 << bit_counts.length) - 1);

		Logger.debug("numLines: {}, gamma: {}, epsilon: {}, power consumption: {}", num_lines.get(), gamma, epsilon,
				gamma * epsilon);
		return gamma * epsilon;
	}

	@Override
	public long part2(Path input) throws IOException {
		int num_bits = Files.lines(input).findFirst().orElseThrow().length();
		int[] init_values = Files.lines(input).mapToInt(line -> Integer.parseInt(line, 2)).toArray();

		int[] values = init_values;
		for (int bit = num_bits - 1; bit >= 0 && values.length > 1; bit--) {
			// Count the number of 1s at position i
			int count = 0;
			for (int x = 0; x < values.length; x++) {
				if ((values[x] & (1 << bit)) != 0) {
					count++;
				}
			}
			final boolean value = count >= (values.length / 2.0);

			// Filter values to only those with bit[i] equal to value
			final int b = bit;
			values = IntStream.of(values).filter(n -> ((n & (1 << b)) != 0) == value).toArray();
		}
		int og_rating = values[0];

		values = init_values;
		for (int bit = num_bits - 1; bit >= 0 && values.length > 1; bit--) {
			// Count the number of 0s at position i
			int count = 0;
			for (int x = 0; x < values.length; x++) {
				if ((values[x] & (1 << bit)) != 0) {
					count++;
				}
			}
			final boolean value = count < (values.length / 2.0);
			Logger.debug("length: {}, count: {}, value: {}", values.length, count, value);

			// Filter values to only those with bit[i] equal to value
			final int b = bit;
			values = IntStream.of(values).filter(n -> ((n & (1 << b)) != 0) == value).toArray();
		}
		int co2s_rating = values[0];

		Logger.debug("oxygen generator rating: {}, CO2 scrubber rating: {}, product: {}", og_rating, co2s_rating,
				og_rating * co2s_rating);
		return og_rating * co2s_rating;
	}
}
