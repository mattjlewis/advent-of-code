package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.diozero.aoc.Day;

public class Day19 extends Day {
	public static void main(String[] args) {
		new Day19().run();
	}

	@Override
	public String name() {
		return "Linen Layout";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<String> lines = Files.readAllLines(input);
		final List<String> towels = Arrays.asList(lines.get(0).split(", "));
		final List<String> designs = lines.subList(2, lines.size());

		return Long.toString(
				designs.stream().mapToLong(design -> countPossibleDesign(design, towels)).filter(r -> r > 0).count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final List<String> lines = Files.readAllLines(input);
		final List<String> towels = Arrays.asList(lines.get(0).split(", "));
		final List<String> designs = lines.subList(2, lines.size());

		return Long.toString(designs.stream().mapToLong(design -> countPossibleDesign(design, towels)).sum());
	}

	private static long countPossibleDesign(String design, List<String> towels) {
		final long[] design_possibilities = new long[design.length()];
		Arrays.fill(design_possibilities, -1);

		return designPossibilities(design, towels, design_possibilities, design.length() - 1);
	}

	private static long designPossibilities(String design, List<String> towels, long[] designPossibilities, int i) {
		if (i < 0) {
			return 1;
		}

		if (designPossibilities[i] != -1) {
			return designPossibilities[i];
		}

		designPossibilities[i] = 0;
		for (String towel : towels) {
			int start_pos = i - towel.length() + 1;
			if (start_pos >= 0 && design.substring(start_pos, i + 1).equals(towel)) {
				long prev_dp = designPossibilities(design, towels, designPossibilities, i - towel.length());
				if (prev_dp > 0) {
					designPossibilities[i] += prev_dp;
				}
			}
		}

		return designPossibilities[i];
	}
}
