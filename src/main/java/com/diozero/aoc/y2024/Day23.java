package com.diozero.aoc.y2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.diozero.aoc.Day;

public class Day23 extends Day {
	public static void main(String[] args) {
		new Day23().run();
	}

	@Override
	public String name() {
		return "LAN Party";
	}

	@Override
	public String part1(final Path input) throws IOException {
		return Long.toString(getLanSet(getNetworkMap(input)).stream()
				.filter(set -> set.stream().anyMatch(s -> s.startsWith("t"))).count());
	}

	@Override
	public String part2(final Path input) throws IOException {
		final Map<String, Set<String>> network_map = getNetworkMap(input);
		final Set<Set<String>> lan_set = getLanSet(network_map);

		for (final Map.Entry<String, Set<String>> entry : network_map.entrySet()) {
			final String computer = entry.getKey();
			Set<String> connections = entry.getValue();

			for (Set<String> lan : lan_set) {
				if (lan.contains(computer)) {
					for (String connection : connections) {
						if (!lan.contains(connection) && network_map.get(connection).containsAll(lan)) {
							lan.add(connection);
						}
					}
				}
			}
		}

		return lan_set.stream().max(Comparator.comparingInt(Set::size)).orElse(Collections.emptySet()).stream().sorted()
				.collect(Collectors.joining(","));
	}

	private static Map<String, Set<String>> getNetworkMap(Path input) throws IOException {
		final Map<String, Set<String>> network_map = new HashMap<>();
		Files.lines(input).map(line -> line.split("-")).forEach(connection -> addConnection(network_map, connection));
		return network_map;
	}

	private static Set<Set<String>> getLanSet(Map<String, Set<String>> networkMap) {
		final Set<Set<String>> lan_set = new HashSet<>();

		for (String computer1 : networkMap.keySet()) {
			Set<String> connections = networkMap.get(computer1);
			for (String computer2 : connections) {
				for (String computer3 : networkMap.get(computer2)) {
					if (connections.contains(computer3)) {
						lan_set.add(new HashSet<>(List.of(computer1, computer2, computer3)));
					}
				}
			}
		}

		return lan_set;
	}

	private static void addConnection(Map<String, Set<String>> networkMap, String[] connection) {
		networkMap.computeIfAbsent(connection[0], k -> new HashSet<>()).add(connection[1]);
		networkMap.computeIfAbsent(connection[1], k -> new HashSet<>()).add(connection[0]);
	}
}
