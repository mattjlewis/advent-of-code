package com.diozero.aoc.y2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;

public class Day14 extends Day {
	private static final String FUEL = "FUEL";
	private static final String ORE = "ORE";

	public static void main(String[] args) {
		new Day14().run();
	}

	@Override
	public String name() {
		return "Space Stoichiometry";
	}

	@Override
	public String part1(Path input) throws IOException {
		final Map<String, Reaction> reactions = Files.lines(input).map(Reaction::parse)
				.collect(Collectors.toMap(recipe -> recipe.output().name(), Function.identity()));

		return Long.toString(getOreRequiredForFuel(1, reactions));
	}

	@Override
	public String part2(Path input) throws IOException {
		final Map<String, Reaction> reactions = Files.lines(input).map(Reaction::parse)
				.collect(Collectors.toMap(recipe -> recipe.output().name(), Function.identity()));
		long ore_available = 1_000_000_000_000L;

		long fuel = 100;
		fuel = ore_available / getOreRequiredForFuel(fuel, reactions) * fuel;
		Logger.debug("approx max fuel: " + fuel);
		while (true) {
			long ore = getOreRequiredForFuel(fuel, reactions);
			if (ore > ore_available) {
				break;
			}

			// fuel = Math.max(fuel + 1, ore_available / ore * fuel);
			fuel++;
		}

		return Long.toString(fuel - 1);
	}

	private static long getOreRequiredForFuel(long amount, Map<String, Reaction> reactions) {
		final Map<String, AtomicLong> chemical_inventory = new HashMap<>();
		getOreRequired(FUEL, amount, reactions, chemical_inventory);
		Logger.trace("Remaining inventory: {}", chemical_inventory);

		return chemical_inventory.getOrDefault(ORE, new AtomicLong()).longValue();
	}

	private static void getOreRequired(String chemical, long amount, Map<String, Reaction> reactions,
			Map<String, AtomicLong> chemicalInventory) {
		if (chemical.equals(ORE)) {
			chemicalInventory.computeIfAbsent(chemical, name -> new AtomicLong()).addAndGet(amount);
			return;
		}

		final Reaction reaction = reactions.get(chemical);
		if (reaction == null) {
			throw new IllegalArgumentException("No reaction found that produces " + chemical);
		}

		// Is there any of this chemical left-over from previous reactions?
		final long current_spare = chemicalInventory.computeIfAbsent(chemical, name -> new AtomicLong()).get();
		if (current_spare > amount) {
			chemicalInventory.get(chemical).addAndGet(-amount);
			return;
		}

		final Chemical output = reaction.output();

		// How many do we need to make taking into account spares?
		final long amount_to_make = amount - current_spare;
		// How many times do we need to run this reaction?
		final long reaction_count = (long) Math.ceil(amount_to_make / (double) output.amount());
		// How many will be left-over after this reaction?
		final long output_spare = reaction_count * output.amount() - amount_to_make;
		chemicalInventory.get(chemical).set(output_spare);

		reaction.inputs().forEach(
				input -> getOreRequired(input.name(), reaction_count * input.amount(), reactions, chemicalInventory));
	}

	private static record Reaction(Chemical output, List<Chemical> inputs) {
		public static Reaction parse(String line) {
			String[] parts = line.split(" => ");

			Pattern p = Pattern.compile("(\\d+) (\\w+)");
			List<Chemical> inputs = p.matcher(parts[0]).results().map(Chemical::create).toList();

			Matcher m = p.matcher(parts[1]);
			if (!m.matches()) {
				throw new IllegalArgumentException("Line '" + line + "' does not match pattern " + p.pattern());
			}

			Chemical output = Chemical.create(m);

			return new Reaction(output, inputs);
		}

		@Override
		public String toString() {
			return String.join(", ", inputs.stream().map(Chemical::toString).toList()) + " => " + output;
		}
	}

	private static record Chemical(String name, int amount) {
		public static Chemical create(Matcher m) {
			return new Chemical(m.group(2), Integer.parseInt(m.group(1)));
		}

		public static Chemical create(MatchResult mr) {
			return new Chemical(mr.group(2), Integer.parseInt(mr.group(1)));
		}

		@Override
		public String toString() {
			return amount + " " + name;
		}
	}
}
