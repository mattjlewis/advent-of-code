package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.tinylog.Logger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.ArrayUtil;
import com.diozero.aoc.util.TextParser;

/*-
 * i  Pattern_index[j]:
 * 0: 1, 2, 3, 0, 1, 2, 3, 0
 * 1: 0, 1, 1, 2, 2, 3, 3, 0
 * 2: 0, 0, 1, 1, 1, 2, 2, 2
 * 3: 0, 0, 0, 1, 1, 1, 1, 2
 * 4: 0, 0, 0, 0, 1, 1, 1, 1
 * 5: 0, 0, 0, 0, 0, 1, 1, 1
 * 6: 0, 0, 0, 0, 0, 0, 1, 1
 * 7: 0, 0, 0, 0, 0, 0, 0, 1
 *
 * i  Pattern[j]:
 * 0: 1, 0, -1, 0, 1, 0, -1, 0
 * 1: 0, 1, 1, 0, 0, -1, -1, 0
 * 2: 0, 0, 1, 1, 1, 0, 0, 0
 * 3: 0, 0, 0, 1, 1, 1, 1, 0
 * 4: 0, 0, 0, 0, 1, 1, 1, 1
 * 5: 0, 0, 0, 0, 0, 1, 1, 1
 * 6: 0, 0, 0, 0, 0, 0, 1, 1
 * 7: 0, 0, 0, 0, 0, 0, 0, 1
 *
 * Sample input (12345678)
 * Phase Signal[i]
 * 0:    1, 2, 3, 4, 5, 6, 7, 8
 * 1:    4, 8, 2, 2, 6, 1, 5, 8
 * 2:    3, 4, 0, 4, 0, 4, 3, 8
 * 3:    0, 3, 4, 1, 5, 5, 1, 8
 * 4:    0, 1, 0, 2, 9, 4, 9, 8
 *
 * Sample input a (80871224585914546619083218645595)
 * Phase Signal[i]
 * 0:    8, 0, 8, 7, 1, 2, 2, 4, 5, 8, 5, 9, 1, 4, 5, 4, 6, 6, 1, 9, 0, 8, 3, 2, 1, 8, 6, 4, 5, 5, 9, 5
 * 1:    2, 4, 7, 0, 6, 8, 6, 1, 3, 0, 0, 6, 0, 3, 8, 7, 8, 2, 6, 5, 6, 6, 8, 5, 3, 2, 4, 8, 4, 9, 4, 5
 * 2:    1, 7, 9, 3, 4, 3, 8, 8, 1, 2, 1, 5, 7, 3, 2, 7, 5, 7, 5, 9, 4, 8, 2, 4, 9, 6, 4, 0, 2, 8, 9, 5
 * 3:    7, 2, 8, 5, 9, 0, 5, 3, 5, 2, 5, 4, 2, 5, 4, 9, 7, 2, 5, 0, 1, 7, 9, 7, 3, 4, 8, 4, 4, 2, 4, 5
 *
 * Simple formula that only works for the second half of the signal (the last number never changes):
 * for (int i = signal.length - 2; i >= signal.length / 2; i--) {
 *  signal[i] = (signal[i] + signal[i + 1]) % 10;
 * }
 *       0  1  2  3  4  5  6  7
 * 0:    1, 2, 3, 4, 5, 6, 7, 8
 * 0.6:  1, 2, 3, 4, 5, 6, 5, 8 (7 + 8) % 10 = 5
 * 0.5:  1, 2, 3, 4, 5, 1, 5, 8 (6 + 5) % 10 = 1
 * 0.4:  1, 2, 3, 4, 6, 1, 5, 8 (5 + 1) % 10 = 6
 *
 * 1:    4, 8, 2, 2, 6, 1, 5, 8
 * 2:    3, 4, 0, 4, 0, 4, 3, 8
 * 3:    0, 3, 4, 1, 5, 5, 1, 8
 * 4:    0, 1, 0, 2, 9, 4, 9, 8
 */
public class Day16 extends Day {
	private static final int[] BASE_PATTERN = { 0, 1, 0, -1 };
	private static final int NUM_PHASES = 100;

	public static void main(String[] args) {
		new Day16().run();
	}

	@Override
	public String name() {
		return "Flawed Frequency Transmission";
	}

	@Override
	public String part1(Path input) throws IOException {
		int[] input_signal = TextParser.loadFirstLineAsIntArray(input);
		Logger.debug("Input signal: {}", Arrays.toString(input_signal));

		final int num_phases = input_signal.length == 8 ? 4 : NUM_PHASES;

		for (int phase = 0; phase < num_phases; phase++) {
			int[] next_input_signal = new int[input_signal.length];
			for (int i = 0; i < input_signal.length; i++) {
				int sum = 0;
				for (int j = 0; j < input_signal.length; j++) {
					sum += input_signal[j] * BASE_PATTERN[((j + 1) / (i + 1)) % BASE_PATTERN.length];
				}
				next_input_signal[i] = Math.abs(sum) % 10;
			}
			Logger.debug("After phase {}: {}", Integer.valueOf(phase + 1), Arrays.toString(next_input_signal));
			input_signal = next_input_signal;
		}

		return Arrays.stream(input_signal, 0, 8).mapToObj(Integer::toString).reduce("", (a, b) -> a + b);
	}

	@Override
	public String part2(Path input) throws IOException {
		final int signal_repetitions = 10_000;

		int[] signal = TextParser.loadFirstLineAsIntArray(input);
		int offset = Integer.parseInt(Files.lines(input).findFirst().orElseThrow().substring(0, 7));

		// The formula only works for the second half of the signal
		if (offset < signal.length * signal_repetitions / 2 || offset > signal.length * signal_repetitions) {
			Logger.error("Cannot compute as offset ({}) must be {}..{}", offset, signal.length * signal_repetitions / 2,
					signal.length * signal_repetitions);
			return null;
		}

		// We will be working backwards to the offset position, so don't need to
		// repeat the input signal the full 10,000 times
		final int num_repetitions = signal_repetitions - offset / signal.length;
		// Only need to iterate back to this offset position within the expanded signal
		offset = offset % signal.length;

		// Now repeat the signal the required number of times
		signal = ArrayUtil.repeat(signal, num_repetitions);

		for (int phase = 0; phase < NUM_PHASES; phase++) {
			// Iterate backwards through the expanded signal up to the recalculated offset
			for (int i = signal.length - 2; i >= offset; i--) {
				signal[i] = (signal[i] + signal[i + 1]) % 10;
			}
		}

		return Arrays.stream(signal, offset, offset + 8).mapToObj(Integer::toString).reduce("", (a, b) -> a + b);
	}
}
