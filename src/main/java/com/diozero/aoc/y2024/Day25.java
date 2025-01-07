package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day25 extends Day {
	public static void main(String[] args) {
		new Day25().run();
	}

	@Override
	public String name() {
		return "Code Chronicle";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<List<Integer>> locks = new ArrayList<>();
		final List<List<Integer>> keys = new ArrayList<>();

		for (String lock_or_key : Files.readString(input).split("\\n\\n")) {
			final char[][] grid = TextParser.loadCharMatrix(Arrays.stream(lock_or_key.split("\\n")));
			if (grid[0][0] == TextParser.SET_CHAR) {
				// Lock
				locks.add(IntStream
						.range(0, grid[0].length).map(col -> IntStream.range(0, grid.length)
								.takeWhile(y -> grid[y][col] == TextParser.SET_CHAR).max().getAsInt())
						.boxed().toList());
			} else {
				// Key
				keys.add(IntStream.range(0, grid[0].length)
						.map(col -> grid.length - 2 - IntStream.range(0, grid.length)
								.takeWhile(y -> grid[y][col] == TextParser.UNSET_CHAR).max().getAsInt())
						.boxed().toList());
			}
		}

		int count = 0;
		for (List<Integer> lock : locks) {
			for (List<Integer> key : keys) {
				if (IntStream.range(0, lock.size())
						.allMatch(i -> lock.get(i).intValue() + key.get(i).intValue() <= 5)) {
					count++;
				}
			}
		}

		return Integer.toString(count);
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Day.NOT_APPLICABLE;
	}
}
