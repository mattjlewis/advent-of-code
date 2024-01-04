package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.diozero.aoc.Day;

public class Day9 extends Day {
	public static void main(String[] args) {
		new Day9().run();
	}

	@Override
	public String name() {
		return "Mirage Maintenance";
	}

	private static Stream<Dataset> load(Path input) throws IOException {
		return Files.lines(input).map(line -> line.split("\\s+"))
				.map(parts -> Arrays.stream(parts).mapToInt(Integer::parseInt).toArray()).map(Dataset::new);
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(load(input).mapToInt(Dataset::nextValue).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(load(input).mapToInt(Dataset::previousValue).sum());
	}

	private static class Dataset {
		private final int[] values;
		private Dataset child;

		public Dataset(int[] values) {
			this.values = values;
			if (Arrays.stream(values).anyMatch(i -> i != 0)) {
				child = new Dataset(
						IntStream.range(0, values.length - 1).map(i -> values[i + 1] - values[i]).toArray());
			}
		}

		public int nextValue() {
			return values[values.length - 1] + (child == null ? 0 : child.nextValue());
		}

		public int previousValue() {
			return values[0] - (child == null ? 0 : child.previousValue());
		}
	}
}
