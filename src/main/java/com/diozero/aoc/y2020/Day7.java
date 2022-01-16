package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day7 extends Day {
	public static void main(String[] args) {
		new Day7().run();
	}

	@Override
	public String name() {
		return "Handy Haversacks";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final List<Rule> rules = Files.lines(input).map(Rule::parse).toList();

		return Integer.toString(contains("shiny gold", rules, new HashSet<>()));
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Map<String, Map<String, Integer>> rules = Files.lines(input).map(Rule::parse)
				.collect(Collectors.toMap(r -> r.colour(), r -> r.contents()));

		return Integer.toString(contents("shiny gold", rules));
	}

	private static int contains(final String colour, final List<Rule> rules, final Set<String> visited) {
		visited.add(colour);

		int count = 0;

		for (Rule rule : rules) {
			if (rule.contents().containsKey(colour) && !visited.contains(rule.colour())) {
				count += 1 + contains(rule.colour(), rules, visited);
			}
		}

		return count;
	}

	private static int contents(final String colour, final Map<String, Map<String, Integer>> rules) {
		int count = 0;

		for (Map.Entry<String, Integer> rule : rules.get(colour).entrySet()) {
			count += rule.getValue().intValue() + rule.getValue().intValue() * contents(rule.getKey(), rules);
		}

		return count;
	}

	private static record Rule(String colour, Map<String, Integer> contents) {
		private static final Pattern PATTERN = Pattern.compile("(.*) bags contain (.*)\\.");
		private static final Pattern BAG_PATTERN = Pattern.compile("(\\d+) (.*) bags?");

		public static Rule parse(final String line) {
			Matcher matcher = PATTERN.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Line '" + line + "' doesn't match pattern " + PATTERN.pattern());
			}
			String colour = matcher.group(1);

			Map<String, Integer> contents = new HashMap<>();
			if (!matcher.group(2).equals("no other bags")) {
				contents = Arrays.stream(matcher.group(2).split(", ")).map(s -> BAG_PATTERN.matcher(s))
						.filter(Matcher::matches)
						.collect(Collectors.toMap(m -> m.group(2), m -> Integer.valueOf(m.group(1))));
			}

			return new Rule(colour, contents);
		}
	}
}
