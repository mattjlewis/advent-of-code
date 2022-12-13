package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.diozero.aoc.Day;

public class Day5 extends Day {
	// move (\\d+) from (\\d+) to (\\d+)
	private static final Pattern MOVE_REGEX = Pattern.compile("(\\d+)");

	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public String name() {
		return "Supply Stacks";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return solve(input, false);
	}

	@Override
	public String part2(final Path input) throws IOException {
		return solve(input, true);
	}

	private static String solve(final Path input, boolean bulkMove) throws IOException {
		boolean reading_crates = true;
		final Map<Integer, List<Character>> stacks = new HashMap<>();
		for (String line : Files.readAllLines(input)) {
			if (line.isBlank()) {
				reading_crates = false;
				continue;
			}

			if (reading_crates) {
				for (int i = 0; i <= line.length() / 4; i++) {
					String crate = line.substring(i * 4, (i + 1) * 4 - 1).trim();
					if (!crate.isBlank()) {
						if (crate.startsWith("[")) {
							stacks.computeIfAbsent(Integer.valueOf(i), key -> new ArrayList<>())
									.add(Character.valueOf(crate.charAt(1)));
						} else {
							Integer stack = Integer.valueOf(crate);
							if (stack.intValue() != i + 1) {
								throw new RuntimeException("Expected stack " + (i + 1) + ", got '" + crate + "'");
							}
							stacks.computeIfAbsent(stack, key -> new ArrayList<>());
						}
					}
				}
			} else {
				final int[] moves = MOVE_REGEX.matcher(line).results().mapToInt(mr -> Integer.parseInt(mr.group()))
						.toArray();
				final List<Character> from = stacks.get(Integer.valueOf(moves[1] - 1));
				final List<Character> to = stacks.get(Integer.valueOf(moves[2] - 1));
				if (bulkMove) {
					IntStream.range(0, moves[0]).forEach(i -> to.add(0, from.remove(moves[0] - i - 1)));
				} else {
					IntStream.range(0, moves[0]).forEach(i -> to.add(0, from.remove(0)));
				}
			}
		}

		return stacks.values().stream().filter(q -> !q.isEmpty()).map(list -> list.get(0)).map(Object::toString)
				.collect(Collectors.joining());
	}
}
