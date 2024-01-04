package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day1 extends Day {
	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public String name() {
		return "Sonar Sweep";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final AtomicInteger count = new AtomicInteger();
		final AtomicInteger last_depth = new AtomicInteger(Integer.MAX_VALUE);
		Files.lines(input).mapToInt(Integer::parseInt).forEach(val -> update(val, count, last_depth));

		return Integer.toString(count.get());
	}

	private static void update(int depth, AtomicInteger count, AtomicInteger lastDepth) {
		if (depth > lastDepth.get()) {
			count.incrementAndGet();
		}

		lastDepth.set(depth);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final int[] numbers = TextParser.loadIntArray(input);
		int part2 = 0;
		for (int i = 1; i < numbers.length; i++) {
			// (t[0] + t[1] + t[2]) < (t[1] + t[2] + t[3]) = t[0] < t[3]
			if (i > 2 && numbers[i] > numbers[i - 3]) {
				part2++;
			}
		}
		return Integer.toString(part2);
	}
}
