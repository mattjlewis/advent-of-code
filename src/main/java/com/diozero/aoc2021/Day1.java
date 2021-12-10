package com.diozero.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.diozero.aoc2021.util.AocBase;

public class Day1 extends AocBase {
	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public long part1(Path input) throws IOException {
		final AtomicInteger count = new AtomicInteger();
		final AtomicInteger last_depth = new AtomicInteger(Integer.MAX_VALUE);
		Files.lines(input).mapToInt(Integer::valueOf).forEach(val -> update(val, count, last_depth));

		return count.get();
	}

	private static void update(int depth, AtomicInteger count, AtomicInteger lastDepth) {
		if (depth > lastDepth.get()) {
			count.incrementAndGet();
		}

		lastDepth.set(depth);
	}

	@Override
	public long part2(Path input) throws IOException {
		int[] numbers = Files.lines(input).mapToInt(Integer::valueOf).toArray();
		int part2 = 0;
		for (int i = 1; i < numbers.length; i++) {
			// (t[0] + t[1] + t[2]) < (t[1] + t[2] + t[3]) = t[0] < t[3]
			if (i > 2 && numbers[i] > numbers[i - 3]) {
				part2++;
			}
		}
		return part2;
	}
}
