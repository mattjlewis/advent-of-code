package com.diozero.aoc.y2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;

public class Day14 extends Day {
	public static void main(String[] args) {
		new Day14().run();
	}

	@Override
	public String name() {
		return "Extended Polymerization";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final Puzzle puzzle = loadData(input);

		int num_steps = 10;

		// Brute force dumb solution that doesn't scale
		final StringBuilder text = new StringBuilder(puzzle.start());
		for (int step = 0; step < num_steps; step++) {
			for (int pos = 0; pos < text.length() - 1; pos++) {
				final Character insertion = puzzle.rules().get(text.substring(pos, pos + 2)).insertion();
				if (insertion != null) {
					text.insert(pos + 1, insertion.charValue());
					pos++;
				}
			}
			Logger.debug("text: {}", text);
		}

		final Map<Character, Long> counts = text.chars().mapToObj(ch -> Character.valueOf((char) ch))
				.collect(Collectors.groupingBy(ch -> ch, Collectors.counting()));
		final long min = counts.entrySet().stream().min((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.orElseThrow().getValue().longValue();
		final long max = counts.entrySet().stream().max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.orElseThrow().getValue().longValue();
		Logger.debug("min: {}, max: {}", min, max);

		return Long.toString(max - min);
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Puzzle puzzle = loadData(input);

		// Exponential growth so can't repeat part1 brute force solution :-)

		// Count the number of letters in the starting string
		// Store as AtomicLong so that they can be updated efficiently thereby avoiding
		// Long.valueOf(long)
		final Map<Character, AtomicLong> char_counts = puzzle.start().chars()
				.mapToObj(ch -> Character.valueOf((char) ch))
				.collect(Collectors.groupingBy(ch -> ch, Collectors.counting())).entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> new AtomicLong(e.getValue().longValue())));

		// Count the number of pairs in the starting text
		Map<String, AtomicLong> pair_counts = new HashMap<>();
		for (int pos = 0; pos < puzzle.start().length() - 1; pos++) {
			final String pair = puzzle.start().substring(pos, pos + 2);
			pair_counts.computeIfAbsent(pair, p -> new AtomicLong(0)).incrementAndGet();
		}
		Logger.debug("start: {}, pair_counts: {}, char_counts: {}", puzzle.start(), pair_counts, char_counts);

		/*-
		 * NNCB {NN=1, NC=1, CB=1}
		 * NCNBCHB {NC=1, CN=1, NB=1, BC=1, CH=1, HB=1}
		 * NBCCNBBBCBHCB {NB=2, BC=2, CC=1, CN=1, BB=2, CB=2, BH=1, HC=1}
		 * NBBBCNCCNBBNBNBBCHBHHBCHB {NB=4, BB=4, BC=3, CN=2, NC=1, CC=1, BN=2, CH=2, HB=3, BH=1, HH=1}
		 *                           {NB=4, BB=4, BC=3, CN=2, NC=1, CC=1, BN=2, CH=2, HB=3, BH=1, HH=1}
		 */
		final int num_steps = 40;
		for (int step = 0; step < num_steps; step++) {
			final Map<String, AtomicLong> new_pair_counts = new HashMap<>();
			pair_counts.entrySet().forEach(
					e -> update(char_counts, new_pair_counts, puzzle.rules().get(e.getKey()), e.getValue().get()));

			pair_counts = new_pair_counts;
			Logger.debug("step: {}, pair_counts: {}, char_counts: {}", step, pair_counts, char_counts);
		}

		final long min = char_counts.entrySet().stream()
				.min((e1, e2) -> Long.compare(e1.getValue().get(), e2.getValue().get())).orElseThrow().getValue().get();
		final long max = char_counts.entrySet().stream()
				.max((e1, e2) -> Long.compare(e1.getValue().get(), e2.getValue().get())).orElseThrow().getValue().get();
		Logger.debug("min: {}, max: {}", min, max);

		return Long.toString(max - min);
	}

	private static Puzzle loadData(final Path input) throws IOException {
		String text = Files.lines(input).findFirst().orElseThrow().trim();

		Map<String, Rule> rules = Files.lines(input).skip(2).map(line -> line.split(" -> "))
				.collect(Collectors.toUnmodifiableMap(rule -> rule[0],
						rule -> Rule.create(rule[0], Character.valueOf(rule[1].charAt(0)))));
		Logger.debug("rules: {}", rules);

		return new Puzzle(text, rules);
	}

	private static void update(final Map<Character, AtomicLong> charCounts, final Map<String, AtomicLong> pairCounts,
			final Rule rule, final long value) {
		final String[] new_pairs = rule.result();
		for (int i = 0; i < new_pairs.length; i++) {
			pairCounts.computeIfAbsent(new_pairs[i], p -> new AtomicLong(0)).addAndGet(value);
		}

		// Added one new character per pair count
		charCounts.computeIfAbsent(rule.insertion(), c -> new AtomicLong(0)).addAndGet(value);
	}

	private static final record Puzzle(String start, Map<String, Rule> rules) {
		//
	}

	private static final record Rule(String match, Character insertion, String[] result) {
		public static Rule create(String match, Character insertion) {
			String[] result = { "" + match.charAt(0) + insertion.charValue(),
					"" + insertion.charValue() + match.charAt(1) };

			return new Rule(match, insertion, result);
		}
	}
}
