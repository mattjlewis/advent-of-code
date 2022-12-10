package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day1 extends Day {
	public static void main(String[] args) {
		new Day1().run();
	}

	@Override
	public String name() {
		return "Calorie Counting";
	}

	private static final IntStream load(final Path input) throws IOException {
		final Deque<AtomicInteger> weights = new ArrayDeque<>();
		weights.add(new AtomicInteger());
		Files.lines(input).forEach(line -> {
			if (line.isBlank()) {
				weights.add(new AtomicInteger());
			} else {
				weights.getLast().addAndGet(Integer.parseInt(line));
			}
		});
		return weights.stream().mapToInt(AtomicInteger::get);
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(load(input).max().orElseThrow());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(
				load(input).boxed().sorted(Collections.reverseOrder()).limit(3).mapToInt(Integer::intValue).sum());
	}
}
