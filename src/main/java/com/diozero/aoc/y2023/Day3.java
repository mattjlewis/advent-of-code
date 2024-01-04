package com.diozero.aoc.y2023;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.TextParser;

public class Day3 extends Day {
	public static void main(String[] args) {
		new Day3().run();
	}

	@Override
	public String name() {
		return "Gear Ratios";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(
				loadData(input).stream().flatMap(p -> p.numbers().stream()).mapToInt(Integer::intValue).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(loadData(input).stream().filter(Part::isEngine)
				.mapToInt(p -> p.numbers().stream().mapToInt(Integer::intValue).reduce(1, (a, b) -> a * b)).sum());
	}

	private static Collection<Part> loadData(Path input) throws IOException {
		final char[][] schematic = TextParser.loadCharMatrix(input);
		final int width = schematic[0].length;
		final int height = schematic.length;

		// 1. Find the parts
		final Map<Integer, Part> parts = new HashMap<>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final char ch = schematic[y][x];
				if (Part.isPart(ch)) {
					parts.put(Integer.valueOf(y * width + x), new Part(ch, new ArrayList<>()));
				}
			}
		}

		// 2. Associate the part numbers
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (Character.isDigit(schematic[y][x])) {
					int num = 0;
					Optional<Part> part = Optional.empty();
					do {
						num = 10 * num + (schematic[y][x] - '0');
						for (int dy = Math.max(y - 1, 0); part.isEmpty() && dy <= Math.min(y + 1, height - 1); dy++) {
							for (int dx = Math.max(x - 1, 0); part.isEmpty()
									&& dx <= Math.min(x + 1, width - 1); dx++) {
								if (dy == y && dx == x) {
									continue;
								}
								if (Part.isPart(schematic[dy][dx])) {
									part = Optional.of(parts.get(Integer.valueOf(dy * width + dx)));
								}
							}
						}
						x++;
					} while (x < width && Character.isDigit(schematic[y][x]));

					final int part_num = num;
					part.ifPresent(p -> p.numbers.add(Integer.valueOf(part_num)));
				}
			}
		}

		return parts.values();
	}

	private static record Part(char part, List<Integer> numbers) {
		public static boolean isPart(char ch) {
			return ch != '.' && !Character.isDigit(ch);
		}

		public boolean isEngine() {
			return part == '*' && numbers.size() == 2;
		}
	}
}
