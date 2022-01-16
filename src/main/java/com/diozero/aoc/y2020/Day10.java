package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day10 extends Day {
	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public String name() {
		return "Adapter Array";
	}

	@Override
	public String part1(final Path input) throws IOException {
		// Use Collectors.toList as we need this list to be mutable
		final List<Integer> values = Files.lines(input).map(Integer::parseInt).sorted().collect(Collectors.toList());

		final int final_joltage = values.stream().mapToInt(Integer::intValue).max().orElseThrow() + 3;
		values.add(Integer.valueOf(final_joltage));

		int count_1 = 0;
		int count_3 = 0;
		int joltage = 0;
		while (joltage < final_joltage && !values.isEmpty()) {
			int next = values.remove(0).intValue();
			int delta = next - joltage;
			if (delta > 3 || delta < 1) {
				throw new IllegalStateException("Error - invalid joltage jump " + delta);
			} else if (delta == 1) {
				count_1++;
			} else if (delta == 3) {
				count_3++;
			}
			joltage = next;
		}

		return Long.toString(count_1 * count_3);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<Integer> values = Files.lines(input).map(Integer::parseInt).sorted().collect(Collectors.toList());
		values.add(0, Integer.valueOf(0));

		return Long.toString(getArrangements(values, new HashMap<>(), Integer.valueOf(0)));
	}

	private static long getArrangements(final List<Integer> values, final Map<Integer, Long> cache, Integer index) {
		if (index.intValue() == values.size() - 1) {
			return 1;
		}

		if (cache.containsKey(index)) {
			return cache.get(index).longValue();
		}

		long result = 0;
		for (int i = index.intValue() + 1; i < values.size(); i++) {
			if (values.get(i).intValue() - values.get(index.intValue()).intValue() > 3) {
				break;
			}

			result += getArrangements(values, cache, Integer.valueOf(i));
		}

		cache.put(index, Long.valueOf(result));

		return result;
	}
}
