package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.tinylog.Logger;

import com.diozero.aoc.Day;

public class Day19 extends Day {
	public static void main(String[] args) {
		new Day19().run();
	}

	@Override
	public String name() {
		return "Monster Messages";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final PuzzleInput puzzle_input = PuzzleInput.parse(input);

		final Pattern p = Pattern
				.compile(puzzle_input.rules().get(Integer.valueOf(0)).buildRegex(puzzle_input.rules()));

		return Long.toString(puzzle_input.values().stream().filter(v -> p.matcher(v).matches()).count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final PuzzleInput puzzle_input = PuzzleInput.parse(input);

		/*-
		 * Completely replace rules 8: 42 and 11: 42 31 with the following:
		 * 8: 42 | 42 8
		 * 11: 42 31 | 42 11 31
		 */
		Rule rule = Rule.parse("8: 42 | 42 8");
		puzzle_input.rules().put(rule.index(), rule);
		rule = Rule.parse("11: 42 31 | 42 11 31");
		puzzle_input.rules().put(rule.index(), rule);

		final Pattern p = Pattern
				.compile(puzzle_input.rules().get(Integer.valueOf(0)).buildRegex(puzzle_input.rules()));

		return Long.toString(puzzle_input.values().stream().filter(v -> p.matcher(v).matches()).count());
	}

	private static record PuzzleInput(Map<Integer, Rule> rules, List<String> values) {
		public static PuzzleInput parse(final Path input) throws IOException {
			final Map<Integer, Rule> rules = new HashMap<>();
			final List<String> values = new ArrayList<>();

			boolean processing_rules = true;
			for (String line : Files.readAllLines(input)) {
				if (line.isBlank()) {
					processing_rules = false;
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				if (processing_rules) {
					Rule rule = Rule.parse(line);
					rules.put(rule.index(), rule);
				} else {
					values.add(line);
				}
			}

			return new PuzzleInput(rules, values);
		}
	}

	private static record Rule(Integer index, Optional<String> literal, Optional<List<List<Integer>>> subRules) {
		public static Rule parse(final String line) {
			String[] parts = line.split(": ");
			Integer index = Integer.valueOf(parts[0]);
			if (parts[1].charAt(0) == '"') {
				return new Rule(index, Optional.of(parts[1].substring(1, parts[1].length() - 1)), Optional.empty());
			}

			return new Rule(index, Optional.empty(), Optional.of(Arrays.stream(parts[1].split(" \\| "))
					.map(s -> Arrays.stream(s.trim().split(" ")).map(Integer::valueOf).toList()).toList()));
		}

		public String buildRegex(final Map<Integer, Rule> rules) {
			return buildRegex(rules, new HashMap<>());
		}

		private String buildRegex(final Map<Integer, Rule> rules, final Map<Integer, String> regexCache) {
			if (literal.isPresent()) {
				return literal.get();
			}

			if (regexCache.containsKey(index)) {
				return regexCache.get(index);
			}

			String left = String.join("", subRules.get().get(0).stream()
					.map(i -> rules.get(i).buildRegex(rules, regexCache)).toArray(String[]::new));

			if (subRules.get().size() == 1) {
				return "(?:" + left + ")";
			}

			List<Integer> right_rule = subRules.get().get(1);
			// Recursive?
			if (right_rule.contains(index)) {
				Logger.debug("Recusion detected for rule {}, subRules {}", index, subRules);
				/*-
				 * 8: 42 | 42 8
				 * 11: 42 31 | 42 11 31
				 */
				if (right_rule.size() == 2) {
					// Special case for part 2's updated rule 8
					return "(?:" + left + "+)";
				}

				/*
				 * Special case for part 2's updated rule 11 which resolves to (42){n}(31){n}. I
				 * don't think it is possible to refer to the previous capture group count hence
				 * this ugly brute force approach. From trial and error, n is at most 4.
				 */
				int max_matches = 4;
				return "(?:" + String.join("|", IntStream.range(1, max_matches + 1)
						.mapToObj(i -> rules.get(right_rule.get(0)).buildRegex(rules, regexCache) + "{" + i + "}"
								+ rules.get(right_rule.get(2)).buildRegex(rules, regexCache) + "{" + i + "}")
						.toArray(String[]::new)) + ")";
			}

			String right = String.join("",
					right_rule.stream().map(i -> rules.get(i).buildRegex(rules, regexCache)).toArray(String[]::new));

			return "(?:" + left + "|" + right + ")";
		}

		@Override
		public String toString() {
			return "Rule [index=" + index + ", " + literal.map(l -> "literal=" + l).orElse("subRules=" + subRules.get())
					+ "]";
		}
	}
}
