package com.diozero.aoc.y2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;
import com.diozero.aoc.util.BitMask;

public class Day10 extends Day {
	public static void main(String[] args) {
		new Day10().run();
	}

	@Override
	public String name() {
		return "Factory";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Integer.toString(
				Files.lines(input).map(Machine::parse).mapToInt(Machine::countFewestPressesIndicatorLights).sum());
	}

	@Override
	public String part2(final Path input) throws IOException {
		return Integer.toString(
				Files.lines(input).map(Machine::parse).mapToInt(Machine::countFewestPressesJoltageLevels).sum());
	}

	private static record IntArray(int[] values) {
		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof IntArray int_array)) {
				return false;
			}
			return Arrays.equals(values, int_array.values);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(values);
		}
	}

	private static record IndicatorLightsCount(Integer indicatorLights, int cost)
			implements Comparable<IndicatorLightsCount> {
		@Override
		public int compareTo(IndicatorLightsCount other) {
			return Integer.compare(cost, other.cost);
		}
	}

	private static record Machine(int numLights, int indicatorLights, int[][] wiringSchematics,
			int[] joltageRequirements) {
		final static Pattern MACHINE_PATTERN = Pattern.compile("^\\[(.+)\\]\\s+(.+)\\s+\\{(.*)\\}$");

		public static Machine parse(String line) {
			// [Indicator lights] ((Wiring schematic),) {Joltage requirements}
			// [.###.#] (0,1,2,3,4) (0,3,4) (0,1,2,4,5) (1,2) {10,11,11,5,10,5}
			final Matcher m = MACHINE_PATTERN.matcher(line);
			if (!m.matches()) {
				throw new IllegalArgumentException(
						String.format("Error: line '%s' does not match pattern '%s'", line, MACHINE_PATTERN.pattern()));
			}

			final int lights = BitMask.parse(m.group(1), '#');
			final int[][] wiring_schematics = Arrays.stream(m.group(2).split(" "))
					.map(schematic -> Arrays.stream(schematic.substring(1, schematic.length() - 1).split(","))
							.mapToInt(Integer::valueOf).toArray())
					.toArray(int[][]::new);
			final int[] joltage_requirements = Arrays.stream(m.group(3).split(",")).mapToInt(Integer::parseInt)
					.toArray();

			return new Machine(m.group(1).length(), lights, wiring_schematics, joltage_requirements);
		}

		public int countFewestPressesIndicatorLights() {
			final Queue<IndicatorLightsCount> open_nodes = new PriorityQueue<>();
			final Set<Integer> closed_nodes = new HashSet<>();

			final Integer start = Integer.valueOf(0);
			closed_nodes.add(start);

			open_nodes.offer(new IndicatorLightsCount(start, 0));

			while (!open_nodes.isEmpty()) {
				final IndicatorLightsCount current = open_nodes.poll();
				if (current.indicatorLights.intValue() == indicatorLights) {
					return current.cost;
				}

				// For each neighbour
				for (int[] wiring_schematic : wiringSchematics) {
					final int new_cost = current.cost() + 1;
					// Toggle indicator lights as per the buttons
					final Integer next_lights = Integer
							.valueOf(BitMask.toggle(current.indicatorLights.intValue(), wiring_schematic));

					final IndicatorLightsCount next = new IndicatorLightsCount(next_lights, new_cost);

					if (!closed_nodes.contains(next_lights) && !open_nodes.contains(next)) {
						open_nodes.offer(next);
					} else if (new_cost < next.cost) {
						if (closed_nodes.contains(next_lights)) {
							closed_nodes.remove(next_lights);
							open_nodes.offer(next);
						}
					}
				}
			}

			return 0;
		}

		public int countFewestPressesJoltageLevels() {
			return solve(new IntArray(joltageRequirements), generatePatterns(), new HashMap<>());
		}

		private Map<IntArray, Map<IntArray, Integer>> generatePatterns() {
			final int num_vars = joltageRequirements.length;
			final int num_buttons = wiringSchematics.length;

			final Map<IntArray, Map<IntArray, Integer>> patterns = new HashMap<>();
			generateParityKeys(new int[num_vars], 0, patterns);

			// 2. Extract Coefficients: Values inside parentheses (1,3), (2), etc.
			final List<IntArray> coefficients = new ArrayList<>();
			for (int[] wiring_schematic : wiringSchematics) {
				final int[] processed_coeff = new int[num_vars];
				for (int index : wiring_schematic) {
					if (index < num_vars) {
						processed_coeff[index] = 1;
					}
				}
				coefficients.add(new IntArray(processed_coeff));
			}

			// To match: for num_pressed_buttons in range(num_buttons+1):
			for (int k = 0; k <= num_buttons; k++) {
				final List<List<Integer>> combinations = new ArrayList<>();
				generateCombinations(combinations, new ArrayList<>(), 0, num_buttons, k);

				for (List<Integer> indices : combinations) {
					final int[] sum = new int[num_vars];
					for (int index : indices) {
						final int[] button_effect = coefficients.get(index).values;
						for (int v = 0; v < num_vars; v++) {
							sum[v] += button_effect[v];
						}
					}

					final IntArray pattern = new IntArray(sum);
					final IntArray parity = getParity(pattern);

					// Only set if not present to keep the lowest cost (lowest k)
					final Map<IntArray, Integer> parity_map = patterns.get(parity);
					if (!parity_map.containsKey(pattern)) {
						parity_map.put(pattern, Integer.valueOf(k));
					}
				}
			}

			return patterns;
		}
	}

	// Helper to replicate itertools.combinations
	private static void generateCombinations(List<List<Integer>> res, List<Integer> temp, int start, int n, int k) {
		if (temp.size() == k) {
			res.add(new ArrayList<>(temp));
			return;
		}
		for (int i = start; i < n; i++) {
			temp.add(Integer.valueOf(i));
			generateCombinations(res, temp, i + 1, n, k);
			temp.remove(temp.size() - 1);
		}
	}

	private static int solve(IntArray goal, Map<IntArray, Map<IntArray, Integer>> patternCosts,
			Map<IntArray, Integer> cache) {
		if (Arrays.stream(goal.values).allMatch(v -> v == 0)) {
			return 0;
		}
		if (cache.containsKey(goal)) {
			return cache.get(goal).intValue();
		}

		int answer = 1_000_000;
		final IntArray parity = getParity(goal);
		final Map<IntArray, Integer> options = patternCosts.getOrDefault(parity, Collections.emptyMap());

		for (Map.Entry<IntArray, Integer> entry : options.entrySet()) {
			final IntArray pattern = entry.getKey();
			final int cost = entry.getValue().intValue();

			if (canFit(pattern, goal)) {
				final int[] next_goal_arr = new int[goal.values.length];
				for (int i = 0; i < goal.values.length; i++) {
					next_goal_arr[i] = (goal.values[i] - pattern.values[i]) / 2;
				}
				answer = Math.min(answer, cost + 2 * solve(new IntArray(next_goal_arr), patternCosts, cache));
			}
		}

		cache.put(goal, Integer.valueOf(answer));

		return answer;
	}

	private static void generateParityKeys(int[] current, int depth, Map<IntArray, Map<IntArray, Integer>> patterns) {
		// Python code: product(range(2), repeat=n)
		// Build all possible combinations of 0s and 1s for the map keys
		if (depth == current.length) {
			patterns.put(new IntArray(current.clone()), new HashMap<>());
			return;
		}
		current[depth] = 0;
		generateParityKeys(current, depth + 1, patterns);
		current[depth] = 1;
		generateParityKeys(current, depth + 1, patterns);
	}

	private static IntArray getParity(IntArray t) {
		final int[] p = new int[t.values.length];
		for (int i = 0; i < t.values.length; i++) {
			p[i] = t.values[i] % 2;
		}
		return new IntArray(p);
	}

	private static boolean canFit(IntArray pattern, IntArray goal) {
		for (int i = 0; i < pattern.values.length; i++) {
			if (pattern.values[i] > goal.values[i]) {
				return false;
			}
		}
		return true;
	}
}
