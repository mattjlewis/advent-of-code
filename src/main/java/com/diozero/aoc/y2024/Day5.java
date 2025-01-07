package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.Tuple2;

public class Day5 extends Day {
	public static void main(String[] args) {
		new Day5().run();
	}

	@Override
	public String name() {
		return "Print Queue";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Tuple2<Map<Integer, Set<Integer>>, List<List<Integer>>> data = load(input);
		return Integer.toString(compute(data.second(), data.first(), true));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Tuple2<Map<Integer, Set<Integer>>, List<List<Integer>>> data = load(input);
		return Integer.toString(compute(data.second(), data.first(), false));
	}

	private static final Tuple2<Map<Integer, Set<Integer>>, List<List<Integer>>> load(Path input)
			throws NumberFormatException, IOException {
		boolean reading_page_ordering_rules = true;
		final Map<Integer, Set<Integer>> page_ordering_rules = new HashMap<>();
		final List<List<Integer>> page_updates = new ArrayList<>();
		for (String line : Files.readAllLines(input)) {
			if (line.isBlank()) {
				reading_page_ordering_rules = false;
				continue;
			}

			if (reading_page_ordering_rules) {
				final String[] parts = line.split("\\|");
				page_ordering_rules.computeIfAbsent(Integer.valueOf(parts[0]), key -> new HashSet<>())
						.add(Integer.valueOf(parts[1]));

				continue;
			}

			page_updates.add(Arrays.stream(line.split(",")).map(Integer::valueOf).toList());
		}

		return new Tuple2<>(page_ordering_rules, page_updates);
	}

	private static final int compute(List<List<Integer>> pageUpdate, Map<Integer, Set<Integer>> pageOrderingRules,
			boolean valid) {
		return pageUpdate.stream().filter(page_update -> isValid(page_update, pageOrderingRules) == valid)
				.map(page_update -> valid ? page_update : sort(page_update, pageOrderingRules))
				.mapToInt(page_update -> page_update.get(page_update.size() / 2).intValue()).sum();
	}

	private static final List<Integer> sort(List<Integer> pageUpdate, Map<Integer, Set<Integer>> pageOrderingRules) {
		return pageUpdate.stream().sorted((p1, p2) -> compare(pageOrderingRules, p1, p2)).toList();
	}

	private static int compare(Map<Integer, Set<Integer>> pageOrderingRules, Integer p1, Integer p2) {
		if (pageOrderingRules.containsKey(p1)) {
			return pageOrderingRules.get(p1).contains(p2) ? -1 : 1;
		}

		return pageOrderingRules.get(p2).contains(p1) ? 1 : -1;
	}

	private static final boolean isValid(List<Integer> pageUpdate, Map<Integer, Set<Integer>> pageOrderingRules) {
		boolean valid = true;
		for (int i = 0; valid && i < pageUpdate.size() - 1; i++) {
			final Set<Integer> pages_after = pageOrderingRules.get(pageUpdate.get(i));
			if (pages_after == null) {
				valid = false;
				break;
			}

			for (int j = i + 1; valid && j < pageUpdate.size(); j++) {
				valid = pages_after.contains(pageUpdate.get(j));
			}
		}

		return valid;
	}
}
