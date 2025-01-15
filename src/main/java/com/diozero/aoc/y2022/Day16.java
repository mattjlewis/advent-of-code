package com.diozero.aoc.y2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diozero.aoc.Day;

public class Day16 extends Day {
	private static final int TIME_TO_ERUPTION = 30;

	public static void main(String[] args) {
		new Day16().run();
	}

	@Override
	public String name() {
		return "Proboscidea Volcanium";
	}

	@Override
	public String part1(Path input) throws IOException {
		final List<Valve> valves = load(Files.readAllLines(input));
		final Map<String, Map<String, Integer>> paths = getPaths(valves);

		return Long.toString(bestPressure(getAllPressures(valves, paths), false));
	}

	@Override
	public String part2(Path input) throws IOException {
		final List<Valve> valves = load(Files.readAllLines(input));
		final Map<String, Map<String, Integer>> paths = getPaths(valves);

		return Long.toString(bestPressure(getAllPressures(valves, paths), true));
	}

	private static record Valve(String name, int flowRate, List<String> tunnels) {
	}

	public static int getFlowOfBitmask(List<Valve> nonzero, int bitmask) {
		int flow = 0;
		for (int i = 0; i < nonzero.size(); i++) {
			if (isOpen(i, bitmask)) {
				flow += nonzero.get(i).flowRate;
			}
		}

		return flow;
	}

	public static boolean isOpen(int index, int bitmask) {
		return (((1 << index) & bitmask) != 0);
	}

	private static List<Valve> load(List<String> input) {
		final List<Valve> valves = new ArrayList<>();
		final Pattern pattern = Pattern.compile("^Valve ([A-Z]{2}) has flow rate=(\\d+); [a-z ]+ ([A-Z, ]+)$");

		for (String line : input) {
			final Matcher m = pattern.matcher(line);
			if (m.matches()) {
				valves.add(new Valve(m.group(1), Integer.parseInt(m.group(2)),
						Arrays.stream(m.group(3).split(", ")).toList()));
			}
		}

		return valves;
	}

	private static Map<String, Map<String, Integer>> getPaths(List<Valve> valves) {
		final Map<String, Map<String, Integer>> paths = new HashMap<>();

		for (Valve v : valves) {
			final Deque<Valve> queue = new LinkedList<>();
			queue.add(v);
			final Map<String, Integer> distances = new HashMap<>();
			distances.put(v.name, Integer.valueOf(0));
			final Set<String> seen = new HashSet<>();
			seen.add(v.name);

			while (!queue.isEmpty()) {
				final Valve cur = queue.poll();
				final int dist_from = distances.get(cur.name).intValue();

				for (String connection : cur.tunnels) {
					if (!seen.contains(connection)) {
						seen.add(connection);
						distances.put(connection, Integer.valueOf(dist_from + 1));
						queue.add(valves.stream().filter(x -> x.name.equals(connection)).findFirst().orElseThrow());
					}
				}
			}

			paths.put(v.name, distances);
		}

		return paths;
	}

	static int[][][] getAllPressures(List<Valve> valves, Map<String, Map<String, Integer>> paths) {
		final ArrayList<Valve> nonzero_flow = new ArrayList<>(valves.stream().filter(x -> x.flowRate > 0).toList());
		final int max_valves = nonzero_flow.size();
		final int max_bitmask = 1 << max_valves;
		final int[][][] max_pressure_grid = new int[TIME_TO_ERUPTION + 1][max_valves][max_bitmask];

		for (int[][] square : max_pressure_grid) {
			for (int[] row : square) {
				Arrays.fill(row, Integer.MIN_VALUE);
			}
		}

		for (int i = 0; i < max_valves; i++) {
			max_pressure_grid[paths.get("AA").get(nonzero_flow.get(i).name).intValue() + 1][i][1 << i] = 0;
		}

		for (int minute = 1; minute <= TIME_TO_ERUPTION; minute++) {
			for (int curPos = 0; curPos < max_valves; curPos++) {
				for (int bitset = 0; bitset < max_bitmask; bitset++) {
					final int potential_flow = getFlowOfBitmask(nonzero_flow, bitset);
					final int new_pressure = max_pressure_grid[minute - 1][curPos][bitset] + potential_flow;
					if (new_pressure > max_pressure_grid[minute][curPos][bitset]) {
						max_pressure_grid[minute][curPos][bitset] = new_pressure;
					}

					if (!isOpen(curPos, bitset)) {
						continue;
					}

					for (int other = 0; other < max_valves; other++) {
						if (isOpen(other, bitset)) {
							continue;
						}

						final int dist_to = paths.get(nonzero_flow.get(curPos).name).get(nonzero_flow.get(other).name)
								.intValue();
						if (minute + dist_to + 1 > 30) {
							continue;
						}

						final int travel_pressure = max_pressure_grid[minute][curPos][bitset]
								+ potential_flow * (dist_to + 1);
						final int new_bitset = bitset | (1 << other);
						if (travel_pressure > max_pressure_grid[minute + dist_to + 1][other][new_bitset]) {
							max_pressure_grid[minute + dist_to + 1][other][new_bitset] = travel_pressure;
						}
					}
				}
			}
		}

		return max_pressure_grid;
	}

	private static int bestPressure(int[][][] maxPressureGrid, boolean isPart2) {
		final int max_minutes = isPart2 ? TIME_TO_ERUPTION - 4 : TIME_TO_ERUPTION;
		final int max_bitmask = maxPressureGrid[0][0].length;
		final int max_valves = maxPressureGrid[0].length;

		int best_pressure = 0;
		if (isPart2) {
			for (int first_bit_mask = 1; first_bit_mask < max_bitmask; first_bit_mask++) {
				for (int second_bit_mask = 1; second_bit_mask < max_bitmask; second_bit_mask++) {
					if ((first_bit_mask & second_bit_mask) != second_bit_mask) {
						continue;
					}

					int me = 0;
					int elephant = 0;

					for (int i = 0; i < max_valves; i++) {
						me = Math.max(me, maxPressureGrid[max_minutes][i][(first_bit_mask & (~second_bit_mask))]);
						elephant = Math.max(elephant, maxPressureGrid[max_minutes][i][second_bit_mask]);
					}

					best_pressure = Math.max(best_pressure, me + elephant);
				}
			}

			return best_pressure;
		}

		return Arrays.stream(maxPressureGrid[max_minutes]).flatMapToInt(Arrays::stream).max().orElseThrow();
	}
}
