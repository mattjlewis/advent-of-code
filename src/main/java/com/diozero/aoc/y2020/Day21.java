package com.diozero.aoc.y2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.tinylog.Logger;

import com.diozero.aoc.Day;

public class Day21 extends Day {
	public static void main(String[] args) {
		new Day21().run();
	}

	@Override
	public String name() {
		return "Allergen Assessment";
	}

	@Override
	public String part1(final Path input) throws IOException {
		final PuzzleInput puzzle_input = PuzzleInput.loadData(input);

		// Count the number of times any of these ingredients appear in any ingredients
		return Long.toString(puzzle_input.inertIngredients().stream()
				.mapToLong(i -> puzzle_input.foods().stream().filter(f -> f.ingredients().contains(i)).count()).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final PuzzleInput puzzle_input = PuzzleInput.loadData(input);

		// Remove all of the inert ingredients
		puzzle_input.foods().forEach(f -> f.ingredients().removeAll(puzzle_input.inertIngredients()));

		final Map<String, String> dangerous_ingredient_list = new HashMap<>();
		final Set<String> allergens_to_resolve = new HashSet<>(puzzle_input.allergens());
		while (dangerous_ingredient_list.size() < puzzle_input.allergens().size()) {
			Iterator<String> it = allergens_to_resolve.iterator();
			while (it.hasNext()) {
				String allergen = it.next();
				final Set<String> ingredients = new HashSet<>(puzzle_input.ingredients());
				puzzle_input.foods().stream().filter(food -> food.allergens().contains(allergen))
						.forEach(f -> ingredients.retainAll(f.ingredients()));
				Logger.debug("Allergen {}: {}", allergen, ingredients);
				if (ingredients.size() == 1) {
					String ingredient = ingredients.iterator().next();
					dangerous_ingredient_list.put(allergen, ingredient);
					// Remove this ingredient from all foods
					puzzle_input.foods().stream().forEach(f -> f.ingredients.removeIf(i -> i.equals(ingredient)));
					it.remove();
				}
			}
		}

		return String.join(",", dangerous_ingredient_list.entrySet().stream()
				.sorted((a, b) -> a.getKey().compareTo(b.getKey())).map(Map.Entry::getValue).toList());
	}

	private static record Food(Set<String> ingredients, Set<String> allergens) {
		private static final Pattern PATTERN = Pattern.compile("(.*) \\(contains (.*)\\)");

		public static Food parse(final String line) {
			Matcher m = PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException("Line '" + line + "' doesn't match pattern " + PATTERN.pattern());
			}

			return new Food(Arrays.stream(m.group(1).split(" ")).collect(Collectors.toSet()),
					Set.of(m.group(2).split(", ")));
		}
	}

	private static record PuzzleInput(List<Food> foods, Set<String> allergens, Set<String> ingredients,
			Set<String> inertIngredients) {
		public static PuzzleInput loadData(final Path input) throws IOException {
			final List<Food> foods = Files.lines(input).map(Food::parse).toList();

			// Don't need .distinct() as Set itself is distinct
			final Set<String> allergens = foods.stream().flatMap(food -> food.allergens().stream())
					.collect(Collectors.toSet());
			final Set<String> ingredients = foods.stream().flatMap(food -> food.ingredients().stream())
					.collect(Collectors.toSet());

			final Set<String> inert_ingredients = new HashSet<>();
			for (String ingredient : ingredients) {
				final Set<String> ingredient_allergens = new HashSet<>(allergens);
				// Find the foods that don't contain this ingredient and remove all of their
				// allergens from the list of all allergens
				foods.stream().filter(f -> !f.ingredients().contains(ingredient)).flatMap(f -> f.allergens().stream())
						.forEach(ingredient_allergens::remove);
				// If the list is empty then this ingredient is inert
				if (ingredient_allergens.isEmpty()) {
					inert_ingredients.add(ingredient);
				}
			}

			Logger.debug("inert_ingredients: {}", inert_ingredients);

			return new PuzzleInput(foods, allergens, ingredients, inert_ingredients);
		}
	}
}
