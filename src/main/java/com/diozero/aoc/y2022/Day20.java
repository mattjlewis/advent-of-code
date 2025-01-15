package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day20 extends Day {
	public static void main(String[] args) {
		new Day20().run();
	}

	@Override
	public String name() {
		return "Grove Positioning System";
	}

	@Override
	public String part1(Path input) throws IOException {
		return Long.toString(solve(load(input, 1), 1));
	}

	@Override
	public String part2(Path input) throws IOException {
		return Long.toString(solve(load(input, 811589153L), 10));
	}

	private static List<Num> load(Path input, long multiplier) throws IOException {
		final AtomicInteger index = new AtomicInteger();
		return Files.lines(input).mapToInt(Integer::parseInt)
				.mapToObj(i -> new Num(index.getAndIncrement(), i * multiplier)).collect(Collectors.toList());
	}

	private static long solve(List<Num> circular, int times) {
		final int size = circular.size();

		for (int j = 0; j < times; j++) {
			IntStream.range(0, size).forEach(i -> move(circular, i));
		}

		int zero_index = IntStream.range(0, size).filter(i -> circular.get(i).value == 0).findFirst().orElseThrow();

		return circular.get((zero_index + 1000) % size).value + circular.get((zero_index + 2000) % size).value
				+ circular.get((zero_index + 3000) % size).value;
	}

	private static void move(List<Num> circular, int id) {
		final int size = circular.size();
		final int index = IntStream.range(0, size).filter(i -> circular.get(i).id == id).findFirst().orElseThrow();
		final Num num = circular.remove(index);
		final int modulo = size - 1;
		final int new_index = (modulo + index + (int) (num.value % modulo)) % modulo;
		circular.add(new_index, num);
	}

	private static record Num(int id, long value) {
	}
}
